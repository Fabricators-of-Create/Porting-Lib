package io.github.fabricators_of_create.porting_lib_build.processor;

import io.github.fabricators_of_create.porting_lib_build.ProjectProcessor;

import io.github.fabricators_of_create.porting_lib_build.tasks.ValidateModuleTask;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

public class ValidationProcessor implements ProjectProcessor {
	@Override
	public void apply(Project project) {
		TaskContainer tasks = project.getTasks();
		Task validateModule = tasks.create("validateModule", ValidateModuleTask.class);
		Task assemble = tasks.getByName("assemble");
		Task validateAw = tasks.getByName("validateAccessWidener");
		assemble.dependsOn(validateAw, validateModule);
	}
}
