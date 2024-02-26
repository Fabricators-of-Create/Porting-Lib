package io.github.fabricators_of_create.porting_lib_build.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.fabricators_of_create.porting_lib_build.PortingLibBuildPlugin;
import io.github.fabricators_of_create.porting_lib_build.Utils;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.FileCopyDetails;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FmjExpander extends FilterReader {
	public static final String RESOURCES = "src/main/resources";
	public static final String FMJ = "fabric.mod.json";
	public static final String TEMPLATE_FMJ = RESOURCES + "/template." + FMJ;


	public static final String PROJECT_NAME_PARAM = "projectName";
	public static final String PROJECT_DIR_PARAM = "projectDir";
	public static final String ROOT_PROJECT_DIR_PARAM = "rootProjectDir";

	private boolean hasExpanded = false;

	private String projectName;
	private String projectDir;
	private String rootProjectDir;

	private Path projectDirPath;
	private Path rootProjectDirPath;

	public FmjExpander(Reader in) {
		super(in);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (!hasExpanded) {
			expand();
		}
		return super.read(cbuf, off, len);
	}

	private void expand() throws IOException {
		try {
			validateParams();
			JsonObject json = JsonParser.parseReader(this.in).getAsJsonObject();
			JsonObject expanded = this.expandFmj(json);
			String asString = PortingLibBuildPlugin.GSON.toJson(expanded);
			this.in = new StringReader(asString);
			this.hasExpanded = true;
		} catch (Throwable t) {
			throw new IOException("Error expanding FMJ", t);
		}
	}

	private void validateParams() {
		if (this.projectName == null) {
			throw new IllegalStateException("projectName is null");
		} else if (this.projectDir == null) {
			throw new IllegalStateException("projectDir is null");
		} else if (this.rootProjectDir == null) {
			throw new IllegalStateException("rootProjectDir is null");
		}

		this.projectDirPath = Paths.get(this.projectDir);
		this.rootProjectDirPath = Paths.get(this.rootProjectDir);
	}

	public JsonObject expandFmj(JsonObject moduleFmj) {
		// load from template, add additional from project
		JsonObject template = makeDefaults();
		for (Map.Entry<String, JsonElement> entry : moduleFmj.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			tryMerge(template, key, value);
		}

		String name = "porting_lib_" + this.projectName;
		Path resources = this.projectDirPath.resolve(RESOURCES);

		// fill in mixins
		String mixinsFileName = name + ".mixins.json";
		Path mixins = resources.resolve(mixinsFileName);
		if (Files.exists(mixins)) {
			JsonArray array = new JsonArray();
			array.add(mixinsFileName);
			template.add("mixins", array);
		}
		// and AW
		String awFileName = name + ".accesswidener";
		Path aw = resources.resolve(awFileName);
		if (Files.exists(aw)) {
			template.addProperty("accessWidener", awFileName);
		}

		return template;
	}

	public void tryMerge(JsonObject root, String key, JsonElement value) {
		JsonElement existing = root.get(key);
		if (existing == null) {
			root.add(key, value);
			return;
		}
		if (value.getClass() != existing.getClass()) {
			// no way to merge, just overwrite
			root.add(key, value);
			return;
		}
		if (value instanceof JsonObject obj) {
			mergeObj(root, key, (JsonObject) existing, obj);
		} else if (value instanceof JsonArray array) {
			mergeArray(root, key, (JsonArray) existing, array);
		} else {
			// overwrite existing
			root.add(key, value);
		}
	}

	public void mergeObj(JsonObject json, String key, JsonObject existing, JsonObject toAdd) {
		for (Map.Entry<String, JsonElement> entry : toAdd.entrySet()) {
			String entryKey = entry.getKey();
			JsonElement value = entry.getValue();
			tryMerge(existing, entryKey, value);
		}
		json.add(key, existing);
	}

	public void mergeArray(JsonObject json, String key, JsonArray existing, JsonArray toAdd) {
		for (JsonElement element : toAdd) {
			existing.add(element);
		}
		json.add(key, existing);
	}

	public JsonObject makeDefaults() {
		Path templateFmj = this.rootProjectDirPath.resolve(TEMPLATE_FMJ);
		return Utils.jsonFromPath(templateFmj);
	}

	public record Applicator(Project project) implements Action<FileCopyDetails> {
		@Override
		public void execute(FileCopyDetails details) {
			Map<String, String> params = Map.of(
					PROJECT_NAME_PARAM, this.project.getName(),
					PROJECT_DIR_PARAM, getDir(this.project),
					ROOT_PROJECT_DIR_PARAM, getDir(this.project.getRootProject())
			);

			details.filter(params, FmjExpander.class);
		}

		private static String getDir(Project project) {
			return project.getLayout().getProjectDirectory().getAsFile().toPath().toString();
		}
	}
}
