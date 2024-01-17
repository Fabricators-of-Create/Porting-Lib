package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PortingLibBuildPlugin implements Plugin<Project> {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void apply(Project project) {
		Task validateModule = project.getTasks().create("validateModule", ValidateModuleTask.class);
		project.afterEvaluate(p -> {
			setupDeduplication(p);
			setupResourceProcessing(p);
			setupValidation(p, validateModule);
		});
	}

	public void setupValidation(Project project, Task validateModule) {
		TaskContainer tasks = project.getTasks();
		Task build = tasks.findByName("build");
		if (build == null)
			throw new IllegalStateException("No build task?");
		Task validateAw = tasks.findByName("validateAccessWidener");
		build.dependsOn(validateAw, validateModule);
	}

	public void setupDeduplication(Project project) {
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

	public void setupResourceProcessing(Project project) {
		TaskContainer tasks = project.getTasks();
		tasks.create("sortAccessWidener", SortAccessWidenerTask.class);

		Task processResources = tasks.findByName("processResources");
		if (processResources == null) {
			throw new IllegalStateException("No processResources task?");
		}

		Task addIcons = tasks.create("addMissingIcons", AddMissingIconsTask.class);
		processResources.finalizedBy(addIcons);

		FileCollection processedResources = processResources.getOutputs().getFiles();
		addIcons.getInputs().files(processedResources);
	}

	public static JsonObject jsonFromPath(Path path) {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
