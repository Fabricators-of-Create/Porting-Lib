package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerExperienceEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
	@ModifyExpressionValue(
			method = "playerTouch",
			at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", ordinal = 0)
	)
	private int onPlayerPickup(int delay, Player player) {
		if (delay != 0)
			return delay;
		boolean allowed = PlayerExperienceEvents.EXP_PICKUP.invoker().onExpPickup(player, (ExperienceOrb) (Object) this);
		// player.takeXpDelay == 0
		// return != 0 to cancel
		return !allowed ? 1 : 0;
	}
}
