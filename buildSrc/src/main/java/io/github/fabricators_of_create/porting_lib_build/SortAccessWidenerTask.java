package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SortAccessWidenerTask extends DefaultTask {
	public static final String PATH = "src/main/resources/";
	public static final String HEADER = "accessWidener v2 named\n";

	@TaskAction
	public void sortAccessWidener() throws IOException {
		Project project = getProject();
		String fileName = "porting_lib_" + project.getName() + ".accesswidener";
		Path aw = project.file(PATH + fileName).toPath();
		if (!Files.exists(aw))
			return;
		System.out.println("sorting " + fileName);

		List<String> transitiveClasses = new ArrayList<>();
		List<String> transitiveMethods = new ArrayList<>();
		List<String> transitiveFields = new ArrayList<>();
		List<String> classes = new ArrayList<>();
		List<String> methods = new ArrayList<>();
		List<String> fields = new ArrayList<>();

		List<List<String>> categories = List.of(transitiveClasses, transitiveMethods, transitiveFields, classes, methods, fields);

		try (Stream<String> lines = Files.lines(aw)) {
			lines.forEach(line -> {
				if (line.isBlank() || line.startsWith("#") || line.startsWith("accessWidener"))
					return;
				boolean transitive = line.startsWith("transitive");
				String type = line.split(" ")[1];
				line = '\n' + line;
				switch (type) {
					case "class" -> (transitive ? transitiveClasses : classes).add(line);
					case "method" -> (transitive ? transitiveMethods : methods).add(line);
					case "field" -> (transitive ? transitiveFields : fields).add(line);
					default -> throw new IllegalArgumentException("Unknown type: " + type);
				}
			});
		}

		StringBuilder output = new StringBuilder(HEADER);

		categories.forEach(category -> {
			if (!category.isEmpty()) {
				category.sort(Comparator.naturalOrder());
				category.forEach(output::append);
				output.append('\n');
			}
		});

		Files.writeString(aw, output);
	}
}
