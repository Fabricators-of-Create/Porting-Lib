package io.github.fabricators_of_create.porting_lib_build.processor;

import io.github.fabricators_of_create.porting_lib_build.PortingLibExtension;
import io.github.fabricators_of_create.porting_lib_build.ProjectProcessor;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.util.List;
import java.util.Map;

public class ModuleDependencyProcessor implements ProjectProcessor {
	@Override
	public void apply(Project project) {
		PortingLibExtension extension = project.getExtensions().getByType(PortingLibExtension.class);
		List<String> dependencyNames = extension.getModuleDependencies().get();
		if (dependencyNames.isEmpty())
			return;

		DependencyHandler dependencies = project.getDependencies();
		for (String name : dependencyNames) {
			Dependency dependency = dependencies.project(Map.of(
					"path", ":" + name,
					"configuration", "namedElements"
			));
			dependencies.add("api", dependency);
			dependencies.add("include", dependency);
		}
	}
}
