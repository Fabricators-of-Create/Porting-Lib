package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
  @Inject(method = "upload", at = @At("RETURN"))
  private void port_lib$postStitch(SpriteLoader.Preparations preparations, CallbackInfo ci) {
    TextureStitchCallback.POST.invoker().stitch((TextureAtlas) (Object) this);
  }
}
