package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.client.TextureAtlasSpriteExtension;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteExtension {

	@Shadow
	@Final
	private SpriteContents contents;

	@Override
	public int port_lib$getPixelRGBA(int frameIndex, int x, int y) {
		if (this.contents.animatedTexture != null) {
			x += ((TextureAtlasSprite$AnimatedTextureAccessor) this.contents.animatedTexture).port_lib$getFrameX(frameIndex) * this.contents.width();
			y += ((TextureAtlasSprite$AnimatedTextureAccessor) this.contents.animatedTexture).port_lib$getFrameY(frameIndex) * this.contents.height();
		}

		return this.contents.originalImage.getPixelRGBA(x, y);
	}
}
