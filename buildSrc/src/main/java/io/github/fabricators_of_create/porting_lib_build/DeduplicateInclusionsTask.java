package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
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
				// remove actual jars
				Path jars = root.resolve("META-INF").resolve("jars");
				if (!Files.exists(jars))
					continue;
				List<String> removedNames = new ArrayList<>();
				try (Stream<Path> files = Files.list(jars)) {
					for (Iterator<Path> itr = files.iterator(); itr.hasNext();) {
						Path includedJar = itr.next();
						String name = includedJar.getFileName().toString();
						if (inclusions.contains(name)) {
							Files.delete(includedJar);
							removedNames.add(name);
						} else {
							inclusions.add(name);
							deduplicateInclusions(includedJar, inclusions);
						}
					}
				}
				// remove declarations from FMJ
				Path fmj = root.resolve("fabric.mod.json");
				if (!Files.exists(fmj))
					continue;
				BufferedReader in = Files.newBufferedReader(fmj);
				JsonObject json = JsonParser.parseReader(in).getAsJsonObject();
				in.close();
				JsonArray declarations = json.get("jars").getAsJsonArray();
				JsonArray cleanedDeclarations = new JsonArray();
				for (JsonElement entryElement : declarations) {
					JsonObject entry = entryElement.getAsJsonObject();
					String file = entry.get("file").getAsString();
					String fileName = file.substring("META-INF/jars/".length());
					if (!removedNames.contains(fileName)) {
						cleanedDeclarations.add(entry);
					}
				}
				json.add("jars", cleanedDeclarations);
				// clean contents
				Files.delete(fmj);
				Files.createFile(fmj);
				// write cleaned declarations
				OutputStream out = new BufferedOutputStream(Files.newOutputStream(fmj));
				String data = PortingLibBuildPlugin.GSON.toJson(json);
				out.write(data.getBytes());
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
