package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.fabricators_of_create.porting_lib_build.processor.DeduplicationProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ExtensionConfigurationProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ModuleDependencyProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ResourceProcessingProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ValidationProcessor;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PortingLibBuildPlugin implements Plugin<Project> {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final List<ProjectProcessor> processors = List.of(
			new ExtensionConfigurationProcessor()
	);

	private final List<ProjectProcessor> lateProcessors = List.of(
			new ValidationProcessor(),
			new DeduplicationProcessor(),
			new ResourceProcessingProcessor(),
			new ModuleDependencyProcessor()
	);

	@Override
	public void apply(@NotNull Project project) {
		this.processors.forEach(processor -> processor.apply(project));
		project.afterEvaluate(p -> this.lateProcessors.forEach(processor -> processor.apply(p)));
	}
}
