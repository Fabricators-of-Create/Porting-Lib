package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.texture.SpriteContents;

@Mixin(SpriteContents.AnimatedTexture.class)
public interface TextureAtlasSprite$AnimatedTextureAccessor {
	@Invoker("getFrameX")
	int port_lib$getFrameX(int frameIndex);

	@Invoker("getFrameY")
	int port_lib$getFrameY(int frameIndex);
}
