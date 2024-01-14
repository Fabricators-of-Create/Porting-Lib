package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddMissingIconsTask extends DefaultTask {
	public static final String PATH = "assets/porting_lib/icon.png";
	public static final String DEFAULT_ICON = "src/main/resources/assets/porting_lib/icon.png";

	@TaskAction
	public void addMissingIcons() throws IOException {
		File resources = getInputs().getFiles().getSingleFile();

		File icon = resources.toPath().resolve(PATH).toFile();

		if (!icon.exists()) {
			File defaultIcon = getProject().getRootProject().file(DEFAULT_ICON);
			icon.getParentFile().mkdirs();
			icon.createNewFile();
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(defaultIcon));
				 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(icon))) {
				out.write(in.readAllBytes());
			}
		}
	}
}
