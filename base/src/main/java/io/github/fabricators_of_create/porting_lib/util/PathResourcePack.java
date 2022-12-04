package io.github.fabricators_of_create.porting_lib.util;

import java.nio.file.Path;

/**
 * @deprecated moved to {@link net.minecraftforge.resource.PathResourcePack}
 */
@Deprecated(forRemoval = true)
public class PathResourcePack extends net.minecraftforge.resource.PathResourcePack {
	public PathResourcePack(String packName, Path source) {
		super(packName, source);
	}
}
