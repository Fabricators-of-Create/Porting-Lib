package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface SpriteContentsExtensions {
	default int getPixelRGBA(int frameIndex, int x, int y) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
