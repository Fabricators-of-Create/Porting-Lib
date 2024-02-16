package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.fabricators_of_create.porting_lib_build.processor.DeduplicationProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ValidationProcessor;

import io.github.fabricators_of_create.porting_lib_build.tasks.SortAccessWidenerTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PortingLibBuildPlugin implements Plugin<Project> {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final List<ProjectProcessor> processors = List.of(
			new ValidationProcessor()
	);

	private final List<ProjectProcessor> lateProcessors = List.of(
			new DeduplicationProcessor()
	);

	@Override
	public void apply(@NotNull Project project) {
		project.getExtensions().create("portingLib", PortingLibExtension.class);
		project.getTasks().register("sortAccessWidener", SortAccessWidenerTask.class);

		this.processors.forEach(processor -> processor.apply(project));
		
		project.afterEvaluate(p -> this.lateProcessors.forEach(processor -> processor.apply(p)));
	}
}
