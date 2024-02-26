package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface AbstractTextureExtensions {
	default void setBlurMipmap(boolean blur, boolean mipmap) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void restoreLastBlurMipmap() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
