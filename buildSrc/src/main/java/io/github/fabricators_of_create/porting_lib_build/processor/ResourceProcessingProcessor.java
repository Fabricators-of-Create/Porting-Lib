package io.github.fabricators_of_create.porting_lib_build.processor;

import io.github.fabricators_of_create.porting_lib_build.ProjectProcessor;

import io.github.fabricators_of_create.porting_lib_build.tasks.AddMissingIconsTask;
import io.github.fabricators_of_create.porting_lib_build.tasks.SortAccessWidenerTask;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskContainer;

public class ResourceProcessingProcessor implements ProjectProcessor {
	@Override
	public void apply(Project project) {
		TaskContainer tasks = project.getTasks();
		tasks.create("sortAccessWidener", SortAccessWidenerTask.class);

		Task processResources = tasks.getByName("processResources");

		Task addIcons = tasks.create("addMissingIcons", AddMissingIconsTask.class);
		processResources.finalizedBy(addIcons);

		FileCollection processedResources = processResources.getOutputs().getFiles();
		addIcons.getInputs().files(processedResources);
	}
}
