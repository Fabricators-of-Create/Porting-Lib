package io.github.fabricators_of_create.porting_lib_build.processor;

import io.github.fabricators_of_create.porting_lib_build.ProjectProcessor;

import io.github.fabricators_of_create.porting_lib_build.tasks.DeduplicateInclusionsTask;

import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;

public class DeduplicationProcessor implements ProjectProcessor {
	@Override
	public void apply(Project project) {
		TaskContainer tasks = project.getTasks();
		// mark standard output as non-deduplicated
		tasks.named("remapJar", Jar.class).configure(remapJar -> remapJar.getArchiveClassifier().convention("duplicated"));

		// based on loom remapJar setup
		tasks.create("deduplicateInclusions", DeduplicateInclusionsTask.class, deduplicate -> {
			Jar remapJar = tasks.named("remapJar", Jar.class).get();
			deduplicate.dependsOn(remapJar);

			deduplicate.getArchiveBaseName().convention(remapJar.getArchiveBaseName());
			deduplicate.getArchiveAppendix().convention(remapJar.getArchiveAppendix());
			deduplicate.getArchiveVersion().convention(remapJar.getArchiveVersion());
			deduplicate.getArchiveExtension().convention(remapJar.getArchiveExtension());

			deduplicate.getDuplicatedJar().convention(remapJar.getArchiveFile());
			project.getArtifacts().add(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME, deduplicate);
			project.getArtifacts().add(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, deduplicate);
		});

		// make build use the deduplication
		tasks.named(BasePlugin.ASSEMBLE_TASK_NAME).configure(
				assemble -> assemble.dependsOn(tasks.named("deduplicateInclusions"))
		);
	}
}
