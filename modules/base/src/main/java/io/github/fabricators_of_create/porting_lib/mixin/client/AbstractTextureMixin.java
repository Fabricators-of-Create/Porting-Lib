package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.AbstractTextureExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.AbstractTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin implements AbstractTextureExtensions {
	@Shadow
	protected boolean blur;
	@Shadow
	protected boolean mipmap;

	@Unique
	private boolean port_lib$lastBlur;
	@Unique
	private boolean port_lib$lastMipmap;

	@Shadow
	public abstract void setFilter(boolean blur, boolean mipmap);

	@Unique
	@Override
	public void setBlurMipmap(boolean blur, boolean mipmap) {
		this.port_lib$lastBlur = this.blur;
		this.port_lib$lastMipmap = this.mipmap;
		setFilter(blur, mipmap);
	}

	@Unique
	@Override
	public void restoreLastBlurMipmap() {
		setFilter(this.port_lib$lastBlur, this.port_lib$lastMipmap);
	}
}
