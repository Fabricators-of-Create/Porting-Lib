package io.github.fabricators_of_create.porting_lib_build.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.fabricators_of_create.porting_lib_build.Utils;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ValidateModuleTask extends DefaultTask {
	@Input
	public abstract Property<String> getProjectName();

	@Input
	@Optional
	public abstract Property<Boolean> getIgnoreName();

	@InputFile
	public abstract RegularFileProperty getReadMe();

	@InputDirectory
	public abstract DirectoryProperty getResources();

	@TaskAction
	public void validateModule() throws IOException {
		String name = this.getProjectName().get();
		boolean ignoreName = this.getIgnoreName().getOrElse(Boolean.FALSE);
		Path resources = this.getResources().get().getAsFile().toPath();
		Path readme = this.getReadMe().get().getAsFile().toPath();

		Path fmj = resources.resolve("fabric.mod.json");
		if (!Files.exists(fmj))
			throw new IllegalStateException(name + " does not have a fabric.mod.json");
		JsonObject json = Utils.jsonFromPath(fmj);

		if (!ignoreName) {
			checkName(name);
			checkModId(name, json);
		}

		checkDescription(json);
		checkAw(name, resources, json);
		checkMixins(name, resources, json);
		checkReadMe(readme, name);
	}

	private void checkName(String name) {
		assertTrue(!name.contains("-"), "Module names should not contain '-'");
		String lowerCase = name.toLowerCase(Locale.ROOT);
		assertTrue(lowerCase.equals(name), "Module names should not contain capital letters");
	}

	private void checkModId(String projectName, JsonObject fmj) {
		String id = fmj.get("id").getAsString();
		assertTrue(id.equals("porting_lib_" + projectName), "Module mod IDs should be in the format 'porting_lib_<name>'");
	}

	private void checkDescription(JsonObject fmj) {
		assertTrue(fmj.has("description"), "FMJ description is not present");
		String desc = fmj.get("description").getAsString();
		assertTrue(!desc.isEmpty(), "FMJ description is empty");
	}

	private void checkAw(String moduleName, Path resources, JsonObject fmj) throws IOException {
		assertTrue(!fmj.has("accessWidener"), "An access widener is specified, despite it being automatic");
		String expectedName = "porting_lib_" + moduleName + ".accesswidener";
		try (Stream<Path> files = Files.list(resources)) {
			files.filter(file -> file.toString().endsWith(".accesswidener"))
					.findFirst()
					.ifPresent(path -> {
						String name = path.getFileName().toString();
						assertTrue(name.equals(expectedName), name + " does not match expected name: " + expectedName);
					});
		}
	}

	private void checkMixins(String moduleName, Path resources, JsonObject fmj) throws IOException {
		assertTrue(!fmj.has("mixins"), "Mixins are specified, despite it being automatic");
		String expectedName = "porting_lib_" + moduleName + ".mixins.json";
		try (Stream<Path> files = Files.list(resources)) {
			files.filter(file -> file.toString().endsWith(".mixins.json"))
					.findFirst()
					.ifPresent(path -> {
						String name = path.getFileName().toString();
						assertTrue(name.equals(expectedName), name + " does not match expected name: " + expectedName);
					});
		}
		Path mixins = resources.resolve(expectedName);
		if (!Files.exists(mixins))
			return;
		JsonObject json = JsonParser.parseString(Files.readString(mixins)).getAsJsonObject();
		List<String> common = getMixinFilenames(json, "mixins");
		List<String> client = getMixinFilenames(json, "client");
		String pkg = json.get("package").getAsString().replace('.', '/');
		if (pkg.isEmpty())
			return;
		Path mixinDir = getProject().file("src/main/java/" + pkg).toPath();
		try (Stream<Path> files = Files.walk(mixinDir)) {
			files.filter(file -> !Files.isDirectory(file) && !file.toString().endsWith("Plugin.java")).forEach(mixin -> {
				String name = mixin.getFileName().toString();
				name = name.substring(0, name.length() - ".java".length());
				boolean present = common.contains(name) || client.contains(name);
				assertTrue(present, mixin + " was not found in the mixins.json");
			});
		}
	}

	private void checkReadMe(Path readMe, String name) throws IOException {
		String formatted = '`' + name + '`'; // `module_name`
		String content = Files.readString(readMe);
		String table = content.split("### Modules")[1].split("### Contributing")[0];
		String[] lines = table.split("\n");
		for (String line : lines) {
			line = line.trim(); // remove \n
			if (line.isBlank() || line.contains("--------")) // skip separator and empty
				continue;
			line = line.substring(1, line.length() - 1); // cut off | from edges
			String module = line.split("\\|")[0].trim();
			if (module.equals(formatted))
				return; // found it
		}
		assertTrue(false, "Module does not have an entry in the README table");
	}

	private static List<String> getMixinFilenames(JsonObject mixins, String side) {
		if (!mixins.has(side))
			return List.of();
		return StreamSupport.stream(mixins.getAsJsonArray(side).spliterator(), false)
				.map(element -> {
					String mixin = element.getAsString();
					int lastDot = mixin.lastIndexOf('.');
					return mixin.substring(lastDot + 1);
				}).toList();
	}

	private void assertTrue(boolean value, String message) {
		if (!value) {
			throw new IllegalStateException(message);
		}
	}
}
