package io.github.fabricators_of_create.porting_lib.extensions;

public interface AbstractTextureExtensions {
	void port_lib$setBlurMipmap(boolean blur, boolean mipmap);

	void port_lib$restoreLastBlurMipmap();
}
