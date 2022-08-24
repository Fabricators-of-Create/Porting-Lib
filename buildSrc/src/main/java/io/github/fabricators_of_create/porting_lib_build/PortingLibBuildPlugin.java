package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PortingLibBuildPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.afterEvaluate(p -> {
			setupDeduplication(p);
			setupFmjGeneration(p);
		});
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

	public void setupFmjGeneration(Project project) {
		Task processResources = project.getTasks().findByName("processResources");
		if (processResources == null) {
			throw new IllegalStateException("No processResources task?");
		}
		Task expandFmj = project.getTasks().create("expandFmj", ExpandFmjTask.class);
		processResources.finalizedBy(expandFmj);
		expandFmj.getInputs().files(processResources.getOutputs().getFiles());
	}
}
