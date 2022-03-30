package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
  @Inject(method = "prepareToStitch",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 0,
      shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
  private void port_lib$preStitch(ResourceManager resourceManager, Stream<ResourceLocation> stream, ProfilerFiller profilerFiller, int i, CallbackInfoReturnable<TextureAtlas.Preparations> cir, Set<ResourceLocation> set) {
    TextureStitchCallback.PRE.invoker().stitch((TextureAtlas) (Object) this, set::add);
  }

  @Inject(method = "reload", at = @At("RETURN"))
  private void port_lib$postStitch(TextureAtlas.Preparations preparations, CallbackInfo ci) {
    TextureStitchCallback.POST.invoker().stitch((TextureAtlas) (Object) this);
  }
}
