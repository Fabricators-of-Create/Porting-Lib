package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PortingLibBuildPlugin implements Plugin<Project> {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void apply(Project project) {
		Config config = project.getExtensions().create("portingLib", Config.class);
		Task validateModule = project.getTasks().create("validateModule", ValidateModuleTask.class);
		project.afterEvaluate(p -> {
			setupDeduplication(p);
			setupResourceProcessing(p, config);
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
		Task remapJar = project.getTasks().findByName("remapJar");
		if (remapJar == null) {
			throw new IllegalStateException("No remapJar task?");
		}
		Task deduplicateInclusions = project.getTasks().create("deduplicateInclusions", DeduplicateInclusionsTask.class);
		remapJar.finalizedBy(deduplicateInclusions);
		deduplicateInclusions.getInputs().files(remapJar.getOutputs().getFiles());
	}

	public void setupResourceProcessing(Project project, Config config) {
		TaskContainer tasks = project.getTasks();
		tasks.create("sortAccessWidener", SortAccessWidenerTask.class);

		Task processResources = tasks.findByName("processResources");
		if (processResources == null) {
			throw new IllegalStateException("No processResources task?");
		}

		if (config.getExpandFmj()) {
			processResources.configure(new FmjExpander.Configurator(project));
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
