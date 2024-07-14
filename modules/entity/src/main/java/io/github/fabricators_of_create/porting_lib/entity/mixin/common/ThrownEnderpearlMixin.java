package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
	public ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F"), cancellable = true)
	private void onEnderPearlLand(HitResult result, CallbackInfo ci, @Share("teleport") LocalRef<EntityTeleportEvent.EnderPearl> eventRef) {
		EntityTeleportEvent.EnderPearl event = EntityHooks.onEnderPearlLand((ServerPlayer) getOwner(), this.getX(), this.getY(), this.getZ(), MixinHelper.cast(this), 5.0F, result);
		eventRef.set(event);
		if (event.isCanceled()) {
			discard();
			ci.cancel();
		}
	}

	@ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/DimensionTransition;<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;)V", ordinal = 0), index = 1)
	private Vec3 modifyTarget(Vec3 vec3, @Share("teleport") LocalRef<EntityTeleportEvent.EnderPearl> eventRef) {
		return eventRef.get().getTarget();
	}

	@ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
	private float modifyAttackDamage(float damage, @Share("teleport") LocalRef<EntityTeleportEvent.EnderPearl> eventRef) {
		// If damage isn't 5 then another mod has changed it (this isn't a great workaround the only proper solution for this is to use asm to check if another mod has modified this and check those modified conditions).
		if (damage != 5.0F)
			return damage;
		return eventRef.get().getAttackDamage();
	}
}
