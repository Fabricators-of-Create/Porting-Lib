package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DeduplicateInclusionsTask extends DefaultTask {
	@TaskAction
	public void deduplicateInclusions() {
		getInputs().getFiles().forEach(builtJar -> {
			List<String> inclusions = new ArrayList<>();
			deduplicateInclusions(builtJar.toPath(), inclusions);
		});
	}

	public static void deduplicateInclusions(Path jarRoot, List<String> inclusions) {
		try (FileSystem buildJarFileSystem = FileSystems.newFileSystem(jarRoot)) {
			for (Path root : buildJarFileSystem.getRootDirectories()) {
				Path jars = root.resolve("META-INF").resolve("jars");
				if (!Files.exists(jars))
					continue;
				try (Stream<Path> files = Files.list(jars)) {
					for (Iterator<Path> itr = files.iterator(); itr.hasNext();) {
						Path includedJar = itr.next();
						String name = includedJar.getFileName().toString();
						if (inclusions.contains(name)) {
							Files.delete(includedJar);
						} else {
							inclusions.add(name);
							deduplicateInclusions(includedJar, inclusions);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
