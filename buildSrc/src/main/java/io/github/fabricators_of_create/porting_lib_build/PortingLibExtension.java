package io.github.fabricators_of_create.porting_lib_build;

import net.fabricmc.loom.api.LoomGradleExtensionAPI;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;

public abstract class PortingLibExtension {
	@Inject
	protected abstract Project getProject();

	public void addModuleDependencies(List<String> names) {
		names.forEach(this::addModuleDependency);
	}

	public void addModuleDependency(String name) {
		Project project = this.getProject();
		DependencyHandler dependencies = project.getDependencies();

		Dependency dependency = dependencies.project(Map.of(
				"path", ":" + name,
				"configuration", "namedElements"
		));
		dependencies.add("api", dependency);
		dependencies.add("include", dependency);

		if (name.equals("mixin_extensions")) {
			// special case, also an AP
			dependencies.add("annotationProcessor", dependency);
		}

		LoomGradleExtensionAPI loom = project.getExtensions().getByType(LoomGradleExtensionAPI.class);
		loom.mods(mods -> mods.register("porting_lib_" + name, settings -> {
			Project depProject = project.project(":" + name);
			SourceSetContainer sourceSets = depProject.getExtensions().getByType(SourceSetContainer.class);
			settings.sourceSet(sourceSets.getByName("main"), depProject);
		}));
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
}
