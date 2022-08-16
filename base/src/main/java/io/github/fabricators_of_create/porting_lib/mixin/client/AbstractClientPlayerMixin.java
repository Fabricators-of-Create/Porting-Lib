package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.FOVModifierCallback;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
  @Inject(method = "getFieldOfViewModifier", at = @At(value = "RETURN", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
  public void newFov(CallbackInfoReturnable<Float> cir, float fov) {
    float newFov = FOVModifierCallback.EVENT.invoker().getNewFOV((Player) (Object) this, fov);
    if(newFov != fov)
      cir.setReturnValue(newFov);
  }
}
