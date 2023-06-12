package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
	public ExperienceOrbMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(method = "playerTouch", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0), cancellable = true)
	private void port_lib$onPlayerPickupXp(Player player, CallbackInfo ci) {
		PlayerEvents.PickupXp pickupXp = new PlayerEvents.PickupXp(player, (ExperienceOrb) (Object) this);
		pickupXp.sendEvent();
		if (pickupXp.isCanceled())
			ci.cancel();
	}
}
