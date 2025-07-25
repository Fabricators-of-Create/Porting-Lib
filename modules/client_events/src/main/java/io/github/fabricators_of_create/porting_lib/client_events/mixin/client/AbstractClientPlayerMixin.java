package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ComputeFovModifierEvent;
import net.minecraft.client.player.AbstractClientPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
	@ModifyReturnValue(method = "getFieldOfViewModifier", at = @At("RETURN"))
	private float port_lib$modifyFovModifier(float original) {
		var event = new ComputeFovModifierEvent((AbstractClientPlayer) (Object) this, original);
		event.sendEvent();

		return event.getNewFovModifier();
	}
}
