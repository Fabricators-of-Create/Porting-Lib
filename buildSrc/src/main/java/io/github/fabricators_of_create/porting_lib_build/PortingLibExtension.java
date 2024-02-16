package io.github.fabricators_of_create.porting_lib_build;

import net.fabricmc.loom.api.LoomGradleExtensionAPI;

import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import javax.inject.Inject;

public abstract class PortingLibExtension {
	@Inject
	protected abstract Project getProject();

	// no implementation, this is handled by ModuleDependencyProcessor in afterEvaluate
	public abstract ListProperty<String> getModuleDependencies();

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
