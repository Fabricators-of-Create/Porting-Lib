package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextureAtlasSprite.AnimatedTexture.class)
public interface TextureAtlasSprite$AnimatedTextureAccessor {
	@Invoker("getFrameX")
	int port_lib$getFrameX(int frameIndex);

	@Invoker("getFrameY")
	int port_lib$getFrameY(int frameIndex);
}
