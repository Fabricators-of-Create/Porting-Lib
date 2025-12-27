package io.github.fabricators_of_create.porting_lib_build;

import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;

import net.fabricmc.loom.util.gradle.SourceSetHelper;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import javax.inject.Inject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PortingLibExtension {

	private static final String DATAGEN_SOURCESET_NAME = "datagen";

	@Inject
	protected abstract Project getProject();

	public void addModuleDependencies(List<String> names) {
		names.forEach(this::addModuleDependency);
	}

	public void addModuleDependency(String name) {
		Project project = this.getProject();
		DependencyHandler dependencies = project.getDependencies();
		SourceSetOutput clientOutput = project.findProject(":" + name).getExtensions().findByType(JavaPluginExtension.class).getSourceSets().getByName("client").getOutput();

		Dependency dependency = dependencies.project(Map.of(
				"path", ":" + name,
				"configuration", "namedElements"
		));
		List<String> modules;
		if (dependencies.getExtensions().findByName("porting_lib_modules") == null) {
			modules = new ArrayList<>();
			dependencies.getExtensions().add("porting_lib_modules", modules);
		} else {
			modules = (List<String>) dependencies.getExtensions().getByName("porting_lib_modules");
		}
		modules.add("porting_lib_" + name);
		dependencies.add("api", dependency);
		dependencies.add("clientImplementation", clientOutput);

		if (name.equals("mixin_extensions")) {
			// special case, also an AP
			dependencies.add("annotationProcessor", dependency);
		}

		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);
		loom.mods(mods -> mods.register("porting_lib_" + name, settings -> {
			Project depProject = project.project(":" + name);
			SourceSetContainer sourceSets = depProject.getExtensions().getByType(SourceSetContainer.class);
			settings.sourceSet(sourceSets.getByName("main"), depProject);
			settings.sourceSet(sourceSets.getByName("client"), depProject);
		}));
	}

	public void configureDatagen(Action<DataGenerationSettings> action) {
		Project project = this.getProject();
		final LoomGradleExtension extension = LoomGradleExtension.get(project);
		final TaskContainer taskContainer = project.getTasks();
		ConfigurationContainer configurations = project.getConfigurations();

		DataGenerationSettings settings = project.getObjects().newInstance(DataGenerationSettings.class);
		settings.getOutputDirectory().set(project.file("src/main/generated"));
		settings.getCreateRunConfiguration().convention(true);
		settings.getCreateSourceSet().convention(false);
		settings.getStrictValidation().convention(false);
		settings.getAddToResources().convention(true);
		settings.getModuleDependencies().convention(List.of());

		action.execute(settings);

		final SourceSet mainSourceSet = SourceSetHelper.getMainSourceSet(project);
		final File outputDirectory = settings.getOutputDirectory().getAsFile().get();

		if (settings.getAddToResources().get()) {
			mainSourceSet.resources(files -> {
				// Add the src/main/generated to the main sourceset's resources.
				Set<File> srcDirs = new HashSet<>(files.getSrcDirs());
				srcDirs.add(outputDirectory);
				files.setSrcDirs(srcDirs);
			});
		}

		// Exclude the cache dir from the output jar to ensure reproducibility.
		taskContainer.getByName(JavaPlugin.JAR_TASK_NAME, task -> {
			Jar jar = (Jar) task;
			jar.exclude(".cache/**");
		});

		taskContainer.getByName(LifecycleBasePlugin.CLEAN_TASK_NAME, task -> {
			Delete clean = (Delete) task;
			clean.delete(outputDirectory);
		});

		String modId = "porting_lib_" + project.getName() + "_datagen";

		if (settings.getCreateSourceSet().get()) {
			SourceSetContainer sourceSets = SourceSetHelper.getSourceSets(project);

			// Create the new datagen sourceset, depend on the main sourceset.
			SourceSet dataGenSourceSet = sourceSets.create(DATAGEN_SOURCESET_NAME, sourceSet -> {
				sourceSet.setCompileClasspath(
						sourceSet.getCompileClasspath()
								.plus(mainSourceSet.getOutput())
				);

				sourceSet.setRuntimeClasspath(
						sourceSet.getRuntimeClasspath()
								.plus(mainSourceSet.getOutput())
				);

				extendsFrom(project, sourceSet.getCompileClasspathConfigurationName(), mainSourceSet.getCompileClasspathConfigurationName());
				extendsFrom(project, sourceSet.getRuntimeClasspathConfigurationName(), mainSourceSet.getRuntimeClasspathConfigurationName());
			});

			Configuration dataGenApi = configurations.create(DATAGEN_SOURCESET_NAME + "Api");
			dataGenApi.extendsFrom(configurations.getByName(JavaPlugin.API_CONFIGURATION_NAME));

			extension.getMods().create(modId, mod -> {
				// Create a classpath group for this mod. Assume that the main sourceset is already in a group.
				DependencyHandler dependencies = project.getDependencies();
				for (String name : settings.getModuleDependencies().get()) {
					Dependency dependency = dependencies.project(Map.of(
							"path", ":" + name,
							"configuration", "namedElements"
					));
					dependencies.add(dataGenApi.getName(), dependency);

					Project depProject = project.project(":" + name);
					SourceSetContainer source = depProject.getExtensions().getByType(SourceSetContainer.class);
					mod.sourceSet(source.getByName("main"), depProject);
				}
				mod.sourceSet(DATAGEN_SOURCESET_NAME);
			});

			extension.createRemapConfigurations(sourceSets.getByName(DATAGEN_SOURCESET_NAME));
		}

		if (settings.getCreateRunConfiguration().get()) {
			extension.getRunConfigs().create("datagen", run -> {
				run.inherit(extension.getRunConfigs().getByName("server"));
				run.setConfigName("Data Generation");

				run.property("fabric-api.datagen");
				run.property("fabric-api.datagen.output-dir", outputDirectory.getAbsolutePath());
				run.runDir("build/datagen");

				run.property("fabric-api.datagen.modid", modId);

				if (settings.getStrictValidation().get()) {
					run.property("fabric-api.datagen.strict-validation", "true");
				}

				if (settings.getCreateSourceSet().get()) {
					run.source(DATAGEN_SOURCESET_NAME);
				}
			});
		}
	}

	public void enableDatagen() {
		Project project = this.getProject();
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);

		SourceSet main = sourceSets.getByName("main");

		loom.runs(container -> container.register("datagen", settings -> {
			settings.client();
			settings.name("Data Generation");
			settings.source(main);
			settings.runDir("build/datagen");

			settings.vmArg("-Dfabric-api.datagen");
			settings.vmArg("-Dfabric-api.datagen.output-dir=" + project.file("src/generated/resources"));
			settings.vmArg("-Dfabric-api.datagen.modid=porting_lib_" + project.getName());

			// enable creation in subprojects
			settings.ideConfigGenerated(true);
		}));

		main.resources(sources -> {
			sources.srcDir("src/generated/resources");
			sources.exclude("src/generated/resources/.cache");
		});
	}

	public void enableTestMod() {
		Project project = this.getProject();
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);

		SourceSet testmod = sourceSets.create("testmod", sourceSet -> {
			SourceSet main = sourceSets.getByName("main");

			FileCollection compileClasspath = sourceSet.getCompileClasspath()
					.plus(main.getCompileClasspath())
					.plus(main.getOutput());
			sourceSet.setCompileClasspath(compileClasspath);

			FileCollection runtimeClasspath = sourceSet.getRuntimeClasspath()
					.plus(main.getRuntimeClasspath())
					.plus(main.getOutput());
			sourceSet.setRuntimeClasspath(runtimeClasspath);
		});

		loom.runs(container -> {
			container.register("testmodClient", settings -> {
				settings.client();
				settings.name("Testmod Client");
				settings.source(testmod);
				settings.runDir("run/test");
			});
			container.register("testmodServer", settings -> {
				settings.server();
				settings.name("Testmod Server");
				settings.source(testmod);
				settings.runDir("run/test_server");
			});
		});
	}

	public interface DataGenerationSettings {
		/**
		 * Contains the output directory where generated data files will be stored.
		 */
		RegularFileProperty getOutputDirectory();

		/**
		 * Contains a boolean indicating whether a run configuration should be created for the data generation process.
		 */
		Property<Boolean> getCreateRunConfiguration();

		/**
		 * Contains a boolean property indicating whether a new source set should be created for the data generation process.
		 */
		Property<Boolean> getCreateSourceSet();

		/**
		 * Contains a boolean property indicating whether strict validation is enabled.
		 */
		Property<Boolean> getStrictValidation();

		/**
		 * Contains a boolean property indicating whether the generated resources will be automatically added to the main sourceset.
		 */
		Property<Boolean> getAddToResources();

		// Broken :why:
		ListProperty<String> getModuleDependencies();
	}

	private static void extendsFrom(Project project, String name, String extendsFrom) {
		final ConfigurationContainer configurations = project.getConfigurations();

		configurations.named(name, configuration -> {
			configuration.extendsFrom(configurations.getByName(extendsFrom));
		});
	}
}
