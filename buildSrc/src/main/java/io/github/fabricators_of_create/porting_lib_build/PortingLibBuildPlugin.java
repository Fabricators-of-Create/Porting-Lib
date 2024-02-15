package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.fabricators_of_create.porting_lib_build.processor.DeduplicationProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ExtensionConfigurationProcessor;
import io.github.fabricators_of_create.porting_lib_build.processor.ValidationProcessor;
import io.github.fabricators_of_create.porting_lib_build.tasks.AddMissingIconsTask;
import io.github.fabricators_of_create.porting_lib_build.tasks.DeduplicateInclusionsTask;
import io.github.fabricators_of_create.porting_lib_build.tasks.SortAccessWidenerTask;
import io.github.fabricators_of_create.porting_lib_build.tasks.ValidateModuleTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PortingLibBuildPlugin implements Plugin<Project> {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final List<ProjectProcessor> earlyProcessors = List.of(
			new ExtensionConfigurationProcessor()
	);

	private final List<ProjectProcessor> processors = List.of(
			new ValidationProcessor(),
			new DeduplicationProcessor()
	);

	@Override
	public void apply(@NotNull Project project) {
		this.earlyProcessors.forEach(processor -> processor.apply(project));
		project.afterEvaluate(p -> this.processors.forEach(processor -> processor.apply(p)));
	}
}
