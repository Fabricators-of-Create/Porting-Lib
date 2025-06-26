package io.github.fabricators_of_create.porting_lib.extensions.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface AbstractTextureExtension {
	default void port_lib$setBlurMipmap(boolean blur, boolean mipmap) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib$restoreLastBlurMipmap() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
