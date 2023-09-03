package io.github.fabricators_of_create.porting_lib_build;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
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
	@Optional
	public abstract Property<Boolean> getIgnoreName();

	@TaskAction
	public void validateModule() throws IOException {
		Project project = getProject();
		Project rootProject = project.getRootProject();
		if (project == rootProject)
			return;
		boolean ignoreName = getIgnoreName().getOrElse(Boolean.FALSE);
		String name = project.getName();
		Path resources = project.file("src/main/resources").toPath();
		Path fmj = resources.resolve("fabric.mod.json");
		if (!Files.exists(fmj))
			throw new IllegalStateException(name + " does not have a fabric.mod.json");
		String content = Files.readString(fmj);
		JsonObject json = JsonParser.parseString(content).getAsJsonObject();
		if (!ignoreName) {
			checkName(name);
			checkModId(name, json);
		}
		checkDescription(json);
		checkAw(name, resources, json);
		checkMixins(name, resources, json);
		checkReadMe(rootProject, name);
	}

	private void checkName(String name) {
		assertTrue(!name.contains("-"), "Module $module should not have a '-' in its name");
		String lowerCase = name.toLowerCase(Locale.ROOT);
		assertTrue(lowerCase.equals(name), "Module $module should not contain capital letters");
	}

	private void checkModId(String projectName, JsonObject fmj) {
		String id = fmj.get("id").getAsString();
		assertTrue(id.equals("porting_lib_" + projectName), "Module mod IDs should be in the format 'porting_lib_<name>");
	}

	private void checkDescription(JsonObject fmj) {
		assertTrue(fmj.has("description"), "Module $module does not have a description");
		String desc = fmj.get("description").getAsString();
		assertTrue(!desc.isEmpty(), "Module $module has an empty description");
	}

	private void checkAw(String moduleName, Path resources, JsonObject fmj) throws IOException {
		assertTrue(!fmj.has("accessWidener"), "Module $module specifies an access widener, despite it being automatic");
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
		assertTrue(!fmj.has("mixins"), "Module $module specifies mixins, despite it being automatic");
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
				assertTrue(present, mixin + " of $module was not found in the mixins.json");
			});
		}
	}

	private void checkReadMe(Project rootProject, String name) throws IOException {
		String formatted = '`' + name + '`'; // `module_name`
		Path readMe = rootProject.file("README.md").toPath();
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
		assertTrue(false, "Module $module does not have an entry in the README table");
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
			String filledMessage = message.replace("$module", getProject().getName());
			throw new IllegalStateException(filledMessage);
		}
	}
}
