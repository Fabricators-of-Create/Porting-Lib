package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.event.client.FieldOfViewEvents;
import net.minecraft.client.player.AbstractClientPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
  @ModifyExpressionValue(
		  method = "getFieldOfViewModifier",
		  at = @At(
				  value = "INVOKE",
				  target = "Lnet/minecraft/util/Mth;lerp(FFF)F"
		  )
  )
  private float port_lib$modifyFovModifier(float fov) {
	  // returns original if unchanged, this is safe
	  return FieldOfViewEvents.MODIFY.invoker().modifyFov((AbstractClientPlayer) (Object) this, fov);
  }
}
