package io.github.fabricators_of_create.porting_lib.resources;

import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.NoSuchElementException;

public class ModPathPackResources extends PathPackResources {

	private final ModContainer mf;

	public ModPathPackResources(ModContainer mf) {
		super(mf.getMetadata().getName(), false, mf.getRootPath());
		this.mf = mf;
	}

	@Override
	protected Path resolve(String... paths) {
		String path = String.join("/", paths);
		return mf.findPath(path).orElseThrow(() -> new NoSuchElementException("Path " + path + " not found"));
	}

}
