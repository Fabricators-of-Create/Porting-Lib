package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.SpriteContentsExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor.TextureAtlasSprite$AnimatedTextureAccessor;
import net.minecraft.client.renderer.texture.SpriteContents;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin implements SpriteContentsExtensions {
	@Shadow
	@Final
	private @Nullable SpriteContents.AnimatedTexture animatedTexture;

	@Shadow
	@Final
	int width;

	@Shadow
	@Final
	int height;

	@Shadow
	NativeImage[] byMipLevel;

	@Override
	public int getPixelRGBA(int frameIndex, int x, int y) {
		if (this.animatedTexture != null) {
			x += ((TextureAtlasSprite$AnimatedTextureAccessor) this.animatedTexture).port_lib$getFrameX(frameIndex) * this.width;
			y += ((TextureAtlasSprite$AnimatedTextureAccessor) this.animatedTexture).port_lib$getFrameY(frameIndex) * this.height;
		}

		return this.byMipLevel[0].getPixelRGBA(x, y);
	}
}
