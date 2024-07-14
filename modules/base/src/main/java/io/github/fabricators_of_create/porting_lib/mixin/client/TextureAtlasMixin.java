package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
	@Inject(method = "upload", at = @At("RETURN"))
	private void postStitch(SpriteLoader.Preparations preparations, CallbackInfo ci) {
		ClientHooks.onTextureAtlasStitched((TextureAtlas) (Object) this);
	}
}
