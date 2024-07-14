package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerXpEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
	public ExperienceOrbMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", ordinal = 1), cancellable = true)
	private void port_lib$onPlayerPickupXp(Player player, CallbackInfo ci) {
		var pickupXp = new PlayerXpEvent.PickupXp(player, (ExperienceOrb) (Object) this);
		pickupXp.sendEvent();
		if (pickupXp.isCanceled())
			ci.cancel();
	}
}
