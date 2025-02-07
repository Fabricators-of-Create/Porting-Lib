package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.TextureAtlasSpriteExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TextureAtlasSprite$AnimatedTextureAccessor;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TextureAtlasSprite.AnimatedTextureAccessor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Sprite.class)
public abstract class TextureAtlasSpriteMixin implements TextureAtlasSpriteExtensions {
	@Shadow
	@Final
	private @Nullable Sprite.Animation animatedTexture;

	@Shadow
	@Final
	int width;

	@Shadow
	@Final
	int height;

	@Shadow
	@Final
	protected NativeImage[] mainImage;

	@Override
	public int getPixelRGBA(int frameIndex, int x, int y) {
		if (this.animatedTexture != null) {
			x += ((AnimatedTextureAccessor) this.animatedTexture).port_lib$getFrameX(frameIndex) * this.width;
			y += ((AnimatedTextureAccessor) this.animatedTexture).port_lib$getFrameY(frameIndex) * this.height;
		}

		return this.mainImage[0].getColor(x, y);
	}
}
