package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {
	// I actually have no idea why this is a thing ._.
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Nullable
	private Player port_lib$unlimitedLastHurtByPlayer = null;

	protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "aiStep", at = @At("HEAD"))
	private void unlimitedLastHurtStep(CallbackInfo ci) {
		// lastHurtByPlayer is cleared after 100 ticks, capture it indefinitely in unlimitedLastHurtByPlayer for LivingExperienceDropEvent
		if (this.lastHurtByPlayer != null) this.port_lib$unlimitedLastHurtByPlayer = lastHurtByPlayer;
		if (this.port_lib$unlimitedLastHurtByPlayer != null && this.port_lib$unlimitedLastHurtByPlayer.isRemoved()) this.port_lib$unlimitedLastHurtByPlayer = null;
	}

	@ModifyArgs(method = "tickDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
	private void dropExperience(Args args) {
		int amount = args.get(2);
		int newAmount = EntityHooks.getExperienceDrop(this, port_lib$unlimitedLastHurtByPlayer, amount);
		if (amount != newAmount) args.set(2, newAmount);
	}
}
