package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.bsh.commands.dir;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DeduplicateInclusionsTask extends DefaultTask {
	@TaskAction
	public void deduplicateInclusions() {
		getInputs().getFiles().forEach(builtJar -> deduplicateInclusions(builtJar.toPath()));
	}

	public void deduplicateInclusions(Path jar) {
		String name = jar.getFileName().toString();
		Path movedJars = jar.resolveSibling(name + "_extractedJars");
		try {
			deleteDir(movedJars);
			Files.createDirectory(movedJars);
			moveInclusions(jar, movedJars);
			reAddInclusions(jar, movedJars);
			deleteDir(movedJars);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Recursively grab all JiJs of the given jar, and move them into the target dir.
	 * Also removes all FMJ JiJ entries.
	 */
	public void moveInclusions(Path jar, Path target) throws IOException {
		log("Moving inclusions for " + jar.toUri());
		try (FileSystem jarFileSystem = FileSystems.newFileSystem(jar)) {
			for (Path root : jarFileSystem.getRootDirectories()) {
				Path jars = root.resolve("META-INF").resolve("jars");
				boolean hasJijs = Files.exists(jars);
				Path fmj = root.resolve("fabric.mod.json");
				boolean hasFmj = Files.exists(fmj);
				if (!hasJijs)
					log("No JiJs");
				if (!hasFmj)
					log("No FMJ");
				if (!hasJijs || !hasFmj)
					return;

				JsonObject json = PortingLibBuildPlugin.jsonFromPath(fmj).getAsJsonObject();
				json.remove("jars");
				Files.writeString(fmj, PortingLibBuildPlugin.GSON.toJson(json));

				// remove the files
				try (Stream<Path> files = Files.list(jars)) {
					for (Iterator<Path> itr = files.iterator(); itr.hasNext();) {
						Path jij = itr.next();
						log("jij: " + jij.getFileName());
						// move all of that jar's JiJs
						moveInclusions(jij, target);
						// and move the jar itself
						tryMove(jij, target);
					}
				}
				log("deleting " + jars.toUri());
				Files.delete(jars);
			}
		}
	}

	/**
	 * Re-add all JiJs in the given directory to the given jar.
	 */
	public void reAddInclusions(Path jar, Path dir) throws IOException {
		// "jars": [
		//    {
		//      "file": "META-INF/jars/serialization-hooks-0.3.23.jar"
		//    }
		// ]
		try (FileSystem jarFileSystem = FileSystems.newFileSystem(jar)) {
			for (Path root : jarFileSystem.getRootDirectories()) {
				Path fmj = root.resolve("fabric.mod.json");
				Path jars = root.resolve("META-INF").resolve("jars");
				Files.createDirectories(jars);

				JsonObject json = PortingLibBuildPlugin.jsonFromPath(fmj).getAsJsonObject();
				JsonArray jarsJson = new JsonArray();

				try (Stream<Path> files = Files.list(dir)) {
					for (Iterator<Path> itr = files.iterator(); itr.hasNext();) {
						Path jij = itr.next();
						String name = jij.getFileName().toString();
						// add to the FMJ
						JsonObject obj = new JsonObject();
						obj.addProperty("file", "META-INF/jars/" + name);
						jarsJson.add(obj);
						// move the file
						tryMove(jij, jars);
					}
				}
				// add the jars array to the FMJ
				json.add("jars", jarsJson);
				// write new FMJ content
				Files.writeString(fmj, PortingLibBuildPlugin.GSON.toJson(json));
			}
		}
	}

	/**
	 * Move the given file into the given directory.
	 * If the directory already contains a file with the same name, it is unchanged, but the original is deleted.
	 */
	public void tryMove(Path file, Path target) throws IOException {
		String name = file.getFileName().toString();
		Path newPath = target.resolve(name);
		if (Files.exists(newPath)) {
			log(newPath.getFileName() + " already exists in target");
			Files.delete(file);
			return;
		}
		log("moving " + name + " to " + newPath);
		Files.move(file, newPath);
	}

	/**
	 * Delete the given directory and everything inside it.
	 */
	public void deleteDir(Path dir) throws IOException {
		if (!Files.exists(dir))
			return;
		Files.walkFileTree(dir, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc != null)
					throw exc;
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void log(String data) {
//		log(data);
	}
}
