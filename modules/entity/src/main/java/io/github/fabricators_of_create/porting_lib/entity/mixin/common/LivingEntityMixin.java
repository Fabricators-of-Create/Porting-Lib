package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.ArrayList;
import java.util.Collection;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.ShieldBlockEvent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents.Fall.FallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityUseItemEvents;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExtensions {
	@Shadow
	protected int lastHurtByPlayerTime;
	@Shadow
	@Nullable
	protected Player lastHurtByPlayer;

	@Shadow
	public abstract ItemStack getUseItem();

	@Shadow
	public abstract int getUseItemRemainingTicks();

	private int port_lib$lootingLevel;

	public LivingEntityMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@ModifyVariable(
			method = "dropAllDeathLoot",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/LivingEntity;lastHurtByPlayerTime:I"
			)
	)
	private int port_lib$grabLootingLevel(int lootingLevel) {
		port_lib$lootingLevel = lootingLevel;
		return lootingLevel;
	}

	@ModifyArgs(
			method = "dropAllDeathLoot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;IZ)V"
			)
	)
	private void port_lib$modifyLootingLevel(Args args) {
		DamageSource source = args.get(0);
		int originalLevel = args.get(1);
		boolean recentlyHit = args.get(2);
		int modifiedLevel = LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(source, (LivingEntity) (Object) this, originalLevel, recentlyHit);
		args.set(1, modifiedLevel);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void port_lib$startCapturingDrops(DamageSource damageSource, CallbackInfo ci) {
		captureDrops(new ArrayList<>());
	}

	@Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
	private void port_lib$dropCapturedDrops(DamageSource source, CallbackInfo ci) {
		Collection<ItemEntity> drops = this.captureDrops(null);
		boolean cancelled = LivingEntityEvents.DROPS.invoker().onLivingEntityDrops(
				(LivingEntity) (Object) this, source, drops, port_lib$lootingLevel, lastHurtByPlayerTime > 0
		);
		if (!cancelled)
			drops.forEach(e -> level().addFreshEntity(e));
	}

	@Unique
	private FallEvent port_lib$currentFallEvent = null;

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$cancelFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		port_lib$currentFallEvent = new FallEvent((LivingEntity) (Object) this, source, fallDistance, multiplier);
		port_lib$currentFallEvent.sendEvent();
		if (port_lib$currentFallEvent.isCanceled()) {
			cir.setReturnValue(true);
		}
	}

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	public float port_lib$modifyDistance(float fallDistance) {
		if (port_lib$currentFallEvent != null) {
			return port_lib$currentFallEvent.getDistance();
		}
		return fallDistance;
	}

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	public float port_lib$modifyMultiplier(float multiplier) {
		if (port_lib$currentFallEvent != null) {
			return port_lib$currentFallEvent.getDamageMultiplier();
		}
		return multiplier;
	}

	@ModifyArgs(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
	private void create$dropExperience(Args args) {
		int amount = args.get(2);
		int newAmount = LivingEntityEvents.EXPERIENCE_DROP.invoker().onLivingEntityExperienceDrop(amount, lastHurtByPlayer, (LivingEntity) (Object) this);
		if (amount != newAmount) args.set(2, newAmount);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"), cancellable = true)
	private void tick(CallbackInfo ci) {
		LivingEntityEvents.LivingTickEvent event = new LivingEntityEvents.LivingTickEvent((LivingEntity) (Object) this);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}

	@ModifyVariable(method = "knockback", at = @At("STORE"), ordinal = 0, argsOnly = true)
	private double port_lib$takeKnockback(double f) {
		if (lastHurtByPlayer != null)
			return LivingEntityEvents.KNOCKBACK_STRENGTH.invoker().onLivingEntityTakeKnockback(f, lastHurtByPlayer);

		return f;
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float port_lib$onHurt(float amount, DamageSource source, float amount2) {
		return LivingEntityEvents.HURT.invoker().onHurt(source, (LivingEntity) (Object) this, amount);
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void onJump(CallbackInfo ci) {
		new LivingEntityEvents.LivingJumpEvent((LivingEntity) (Object) this).sendEvent();
	}

	@Inject(method = "completeUsingItem", at = @At(value = "INVOKE", shift = At.Shift.BY, by = 2, target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$onFinishUsing(CallbackInfo ci, InteractionHand hand, ItemStack result) {
		LivingEntityUseItemEvents.LIVING_USE_ITEM_FINISH.invoker().onUseItem((LivingEntity) (Object) this, this.getUseItem().copy(), getUseItemRemainingTicks(), result);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	public void port_lib$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!((Object) this instanceof Player)) {
			LivingAttackEvent event = new LivingAttackEvent((LivingEntity) (Object) this, source, amount);
			event.sendEvent();
			if (event.isCanceled())
				cir.setReturnValue(false);
		}
	}

	@ModifyReturnValue(method = "getVisibilityPercent", at = @At("RETURN"))
	private double modifyVisibility(double original, @javax.annotation.Nullable Entity pLookingEntity) {
		LivingEntityEvents.LivingVisibilityEvent event = new LivingEntityEvents.LivingVisibilityEvent((LivingEntity) (Object) this, pLookingEntity, original);
		event.sendEvent();
		return Math.max(0, event.getVisibilityModifier());
	}

	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	private void onLivingDeath(DamageSource cause, CallbackInfo ci) {
		LivingDeathEvent event = new LivingDeathEvent((LivingEntity) (Object) this, cause);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
	private boolean shieldBlockEvent(boolean original, DamageSource pSource, float pAmount, @Share("shield_block") LocalRef<ShieldBlockEvent> eventRef) {
		if (original) {
			ShieldBlockEvent event = new ShieldBlockEvent((LivingEntity) (Object) this, pSource, pAmount);
			event.sendEvent();
			eventRef.set(event);
			return !event.isCanceled();
		}
		return original;
	}

	@WrapWithCondition(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	private boolean shieldTakesDamage(LivingEntity entity, float damage, @Share("shield_block") LocalRef<ShieldBlockEvent> eventRef) {
		return eventRef.get().shieldTakesDamage();
	}

	@ModifyVariable(method = "hurt", at = @At(value = "STORE", ordinal = 1), index = 5)
	private float modifyBlockedDamage(float value, @Share("shield_block") LocalRef<ShieldBlockEvent> eventRef) {
		return eventRef.get().getBlockedDamage();
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = {
			"floatValue=0"
	}, ordinal = 2))
	private float modifyActualDamage(float value, DamageSource pSource, float pAmount, @Share("shield_block") LocalRef<ShieldBlockEvent> eventRef) {
		return pAmount - eventRef.get().getBlockedDamage();
	}
}
