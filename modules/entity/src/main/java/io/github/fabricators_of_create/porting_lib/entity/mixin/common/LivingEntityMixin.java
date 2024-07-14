package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingKnockBackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.ShieldBlockEvent;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingFallEvent;

import io.github.fabricators_of_create.porting_lib.entity.events.living.MobEffectEvent;
import io.github.fabricators_of_create.porting_lib.entity.ext.LivingEntityExt;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;

import net.minecraft.world.effect.MobEffectInstance;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

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
public abstract class LivingEntityMixin extends Entity implements LivingEntityExt {
	@Shadow
	protected int lastHurtByPlayerTime;
	@Shadow
	@Nullable
	protected Player lastHurtByPlayer;

	@Shadow
	public abstract ItemStack getUseItem();

	@Shadow
	public abstract int getUseItemRemainingTicks();

	@Shadow
	@Final
	private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

	@Shadow
	protected abstract void onEffectRemoved(MobEffectInstance mobEffectInstance);

	@Shadow
	private boolean effectsDirty;

	@Shadow
	protected ItemStack useItem;

	@Shadow
	public abstract InteractionHand getUsedItemHand();

	@Shadow
	protected int useItemRemaining;

	public LivingEntityMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void port_lib$startCapturingDrops(ServerLevel serverLevel, DamageSource damageSource, CallbackInfo ci) {
		captureDrops(new ArrayList<>());
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void port_lib$dropCapturedDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
		Collection<ItemEntity> drops = this.captureDrops(null);
		if (!EntityHooks.onLivingDrops(MixinHelper.cast(this), source, drops, lastHurtByPlayerTime > 0))
			drops.forEach(e -> level().addFreshEntity(e));
	}

	@Unique
	private LivingFallEvent port_lib$currentFallEvent = null;

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$cancelFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		port_lib$currentFallEvent = new LivingFallEvent((LivingEntity) (Object) this, fallDistance, multiplier);
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
		int reward = args.get(2);
		int newReward = EntityHooks.getExperienceDrop(MixinHelper.cast(this), this.lastHurtByPlayer, reward);
		if (reward != newReward) args.set(2, newReward);
	}

	@ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private double modifyKnockbackStrength(double strength, double ogstrength, double xRatio, double zRatio, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
		LivingKnockBackEvent event = EntityHooks.onLivingKnockBack(MixinHelper.cast(this), (float) strength, xRatio, zRatio);
		eventRef.set(event);
		if (!event.isCanceled() && event.getOriginalStrength() != event.getStrength()) {
			return event.getStrength();
		}
		return strength;
	}

	@ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private double modifyRatioX(double ratioX, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
		var event = eventRef.get();
		if (event.getOriginalRatioX() != event.getRatioX())
			return event.getRatioX();
		return ratioX;
	}

	@ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 2, argsOnly = true)
	private double modifyRatioZ(double ratioZ, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
		var event = eventRef.get();
		if (event.getOriginalRatioZ() != event.getRatioZ())
			return event.getRatioZ();
		return ratioZ;
	}

	@Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"), cancellable = true)
	private void shouldCancelKnockback(double strength, double xRatio, double zRatio, CallbackInfo ci, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
		if (eventRef.get().isCanceled())
			ci.cancel();
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void onHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityHooks.onLivingAttack(MixinHelper.cast(this), damageSource, amount))
			cir.setReturnValue(false);
	}

	@Inject(method = "jumpFromGround", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hasImpulse:Z", shift = At.Shift.AFTER))
	public void onJump(CallbackInfo ci) {
		EntityHooks.onLivingJump(MixinHelper.cast(this));
	}

	@WrapOperation(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack onFinishUsing(ItemStack instance, Level level, LivingEntity livingEntity, Operation<ItemStack> original) {
		return EntityHooks.onItemUseFinish((LivingEntity) (Object) this, getUseItem().copy(), getUseItemRemainingTicks(), original.call(instance, level, livingEntity));
	}

	// We use wrap operation here because we want to fire an additional event inside the event check.
	@WrapOperation(method = "releaseUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;releaseUsing(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"))
	private void onStopUsingItem(ItemStack instance, Level level, LivingEntity livingEntity, int useItemRemaining, Operation<Void> original) {
		if (!EntityHooks.onUseItemStop((LivingEntity) (Object) this, useItem, this.getUseItemRemainingTicks())) {
			ItemStack copy = (Object) this instanceof Player ? useItem.copy() : null;
			original.call(instance, level, livingEntity, useItemRemaining);
			if (copy != null && useItem.isEmpty())
				EntityHooks.onPlayerDestroyItem((Player) (Object) this, copy, getUsedItemHand());
		}
	}

	@ModifyReturnValue(method = "getVisibilityPercent", at = @At("RETURN"))
	private double modifyVisibility(double original, @javax.annotation.Nullable Entity pLookingEntity) {
		return EntityHooks.getEntityVisibilityMultiplier(MixinHelper.cast(this), pLookingEntity, original);
	}

	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	private void onLivingDeath(DamageSource cause, CallbackInfo ci) {
		if (EntityHooks.onLivingDeath(MixinHelper.cast(this), cause))
			ci.cancel();
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
	private boolean shieldBlockEvent(boolean original, DamageSource pSource, float pAmount, @Share("shield_block") LocalRef<ShieldBlockEvent> eventRef) {
		if (original) {
			ShieldBlockEvent ev = EntityHooks.onShieldBlock((LivingEntity) (Object) this, pSource, pAmount);
			eventRef.set(ev);
			return !ev.isCanceled();
		}
		return false;
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

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), index = 2, argsOnly = true)
	private float livingHurtEvent(float amount, DamageSource pDamageSource) {
		return EntityHooks.onLivingHurt((LivingEntity) (Object) this, pDamageSource, amount);
	}

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
	private void shouldCancelHurt(DamageSource damageSource, float amount, CallbackInfo ci) {
		if (amount <= 0)
			ci.cancel();
	}

//	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 6), index = 2) TODO: PORT
//	private float livingDamageEvent(float value, DamageSource pDamageSource) {
//		LivingDamageEvent event = new LivingDamageEvent((LivingEntity) (Object) this, pDamageSource, value);
//		event.sendEvent();
//		if (event.isCanceled())
//			return 0;
//		return event.getAmount();
//	}

	@Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
	private void onRemoveEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
		if (EntityHooks.onEffectRemoved((LivingEntity) (Object) this, effect, null))
			cir.setReturnValue(false);
	}

	@WrapWithCondition(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
	private boolean onRemoveAllEffectEvent(LivingEntity instance, MobEffectInstance effect, @Share("event") LocalBooleanRef eventRef) {
		boolean canceled = EntityHooks.onEffectRemoved((LivingEntity) (Object) this, effect, null);
		eventRef.set(canceled);
		return !canceled;
	}

	@WrapWithCondition(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
	private boolean skipRemove(Iterator<MobEffectInstance> iterator, @Share("event") LocalBooleanRef eventRef) {
		return !eventRef.get();
	}

//	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true) TODO: Re-evaluate hook location on fabric
//	private void isEffectApplicable(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
//		MobEffectEvent.Applicable event = new MobEffectEvent.Applicable((LivingEntity) (Object) this, effectInstance);
//		event.sendEvent();
//		if (event.getResult() != BaseEvent.Result.DEFAULT)
//			cir.setReturnValue(event.getResult() == BaseEvent.Result.ALLOW);
//	}

	@WrapMethod(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
	private boolean onAddEffect(MobEffectInstance mobEffectInstance, Entity entity, Operation<Boolean> original) {
		if (!EntityHooks.canMobEffectBeApplied((LivingEntity) (Object) this, mobEffectInstance)) {
			return false;
		} else {
			return original.call(mobEffectInstance, entity);
		}
	}

	@WrapMethod(method = "forceAddEffect")
	private void canAddEffect(MobEffectInstance mobEffectInstance, Entity entity, Operation<Void> original) {
		if (EntityHooks.canMobEffectBeApplied((LivingEntity) (Object) this, mobEffectInstance)) {
			original.call(mobEffectInstance, entity);
		}
	}

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "JUMP", opcode = Opcodes.IFNONNULL))
	private void onEffectAdded(MobEffectInstance newEffect, Entity entity, CallbackInfoReturnable<Boolean> cir, @Local(index = 3) MobEffectInstance oldEffect) {
		new MobEffectEvent.Added((LivingEntity) (Object) this, oldEffect, newEffect, entity).sendEvent();
	}

	@ModifyExpressionValue(method = "tickEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 0))
	private boolean onEffectExpired(boolean original, @Local(index = 3) MobEffectInstance effect) {
		return !(!original && !new MobEffectEvent.Expired((LivingEntity) (Object) this, effect).post());
	}

	@Override
	public boolean removeEffectsCuredBy(EffectCure cure) {
		if (this.level().isClientSide)
			return false;
		boolean ret = false;
		Iterator<MobEffectInstance> itr = this.activeEffects.values().iterator();
		while (itr.hasNext()) {
			MobEffectInstance effect = itr.next();
			if (effect.getCures().contains(cure) && !EntityHooks.onEffectRemoved((LivingEntity) (Object) this, effect, cure)) {
				this.onEffectRemoved(effect);
				itr.remove();
				ret = true;
				this.effectsDirty = true;
			}
		}
		return ret;
	}

	@ModifyExpressionValue(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private boolean onLivingUseTotem(boolean original, DamageSource source, @Local(index = 3) ItemStack item, @Local(index = 7) InteractionHand hand) {
		return original && EntityHooks.onLivingUseTotem((LivingEntity) (Object) this, source, item, hand);
	}

	@Inject(method = "startUsingItem", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;useItem:Lnet/minecraft/world/item/ItemStack;"))
	private void cacheLastUseItem(InteractionHand interactionHand, CallbackInfo ci, @Share("use_item") LocalRef<ItemStack> lastItem) {
		lastItem.set(this.useItem);
	}

	@WrapOperation(method = "startUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int onStartUsingItem(ItemStack instance, LivingEntity livingEntity, Operation<Integer> original, @Share("old_duration") LocalIntRef oldDuration) {
		oldDuration.set(this.useItemRemaining);
		return EntityHooks.onItemUseStart(livingEntity, instance, original.call(instance, livingEntity));
	}

	@Inject(method = "startUsingItem", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"), cancellable = true)
	private void cancelIfDurationLessThenZero(InteractionHand interactionHand, CallbackInfo ci, @Share("old_duration") LocalIntRef oldDuration) {
		if (this.useItemRemaining < 0) {
			// Restore old useItemRemaining since we fire the event later then neo
			this.useItemRemaining = oldDuration.get();
			ci.cancel(); // Early return for negative values, as that indicates event cancellation.
		}
	}
	@WrapWithCondition(method = "updateUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"))
	private boolean onItemUseTick(ItemStack instance, Level level, LivingEntity livingEntity, int useItemRemainingTicks) {
		if (!instance.isEmpty()) {
			this.useItemRemaining = EntityHooks.onItemUseTick(livingEntity, instance, useItemRemainingTicks);
			return getUseItemRemainingTicks() > 0;
		}
		return true;
	}
}
