package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PortingLibBuildPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.afterEvaluate(p -> {
			Task remapJar = p.getTasks().findByName("remapJar");
			if (remapJar == null) {
				throw new IllegalStateException("No remapJar task?");
			}
			Task deduplicateInclusions = project.getTasks().create("deduplicateInclusions", DeduplicateInclusionsTask.class);
			remapJar.finalizedBy(deduplicateInclusions);
			deduplicateInclusions.getInputs().files(remapJar.getOutputs().getFiles());
		});
	}
}
