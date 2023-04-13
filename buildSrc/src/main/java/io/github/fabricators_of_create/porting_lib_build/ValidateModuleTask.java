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
		if (project == project.getRootProject())
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
		checkMixins(name, resources);
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

	private void checkMixins(String moduleName, Path resources) throws IOException {
		String expectedName = "porting_lib_" + moduleName + ".mixins.json";
		Path mixins = resources.resolve(expectedName);
		if (!Files.exists(mixins))
			return;
		JsonObject json = JsonParser.parseString(Files.readString(mixins)).getAsJsonObject();
		List<String> common = getMixinFilenames(json, "mixins");
		List<String> client = getMixinFilenames(json, "client");
		String pkg = json.get("package").getAsString().replace('.', '/');
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
