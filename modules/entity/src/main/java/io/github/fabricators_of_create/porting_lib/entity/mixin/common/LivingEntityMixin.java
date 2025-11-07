package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import io.github.fabricators_of_create.porting_lib.entity.damage.DamageContainer;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingFallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingKnockBackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingShieldBlockEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.MobEffectEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.ShieldBlockEvent;
import io.github.fabricators_of_create.porting_lib.entity.injects.LivingEntityInjection;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityInjection {
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

	@Shadow
	protected float lastHurt;

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

	// damage events & containers

	@Unique
	protected Stack<DamageContainer> port_lib$damageContainers = new Stack<>();

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
	private void pushNewDamageContainer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		port_lib$damageContainers.push(new DamageContainer(source, amount));

		if (EntityHooks.onEntityIncomingDamage((LivingEntity) (Object) this, port_lib$damageContainers.peek()))
			cir.setReturnValue(false);
	}

	@ModifyVariable(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;noActionTime:I", shift = At.Shift.AFTER), argsOnly = true)
	private float modifyDamage(float value) {
		DamageContainer container = this.port_lib$damageContainers.peek();
		if (value != container.getOriginalDamage())
			return container.getConflictResolver().resolve(DamageContainer.DamageConflictType.NORMAL, value, null);
		return container.getNewDamage();
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
	private boolean checkIsDamageBlocked(boolean original, @Share("shieldEvent") LocalRef<LivingShieldBlockEvent> shieldEvent) {
		shieldEvent.set(EntityHooks.onDamageBlock((LivingEntity) (Object) this, port_lib$damageContainers.peek(), original));

		return shieldEvent.get().getBlocked();
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	private void setBlockedDamageToContainer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Share("shieldEvent") LocalRef<LivingShieldBlockEvent> shieldEvent) {
		port_lib$damageContainers.peek().setBlockedDamage(shieldEvent.get());
	}

	@WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"))
	private void checkShouldHurtCurrentShield(LivingEntity instance, float damageAmount, Operation<Void> original, @Share("shieldEvent") LocalRef<LivingShieldBlockEvent> shieldEvent) {
		if (damageAmount != shieldEvent.get().getOriginalBlockedDamage()) {
			original.call(instance, shieldEvent.get().getDamageContainer().getConflictResolver().resolve(DamageContainer.DamageConflictType.SHIELD, damageAmount, shieldEvent.get())); // Ensure modded damage goes through instead of ours.
		} else if (shieldEvent.get().shieldDamage() > 0) {
			original.call(instance, shieldEvent.get().shieldDamage());
		}
	}

	@Definition(id = "f", local = @Local(type = float.class, ordinal = 2))
	@Definition(id = "amount", local = @Local(type = float.class, ordinal = 0, argsOnly = true))
	@Expression("f = @(amount)")
	@ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
	private float modifyTotalBlockedDamage(float original, @Share("shieldEvent") LocalRef<LivingShieldBlockEvent> shieldEvent) {
		if (original != shieldEvent.get().getOriginalBlockedDamage()) {
			return shieldEvent.get().getDamageContainer().getConflictResolver().resolve(DamageContainer.DamageConflictType.SHIELD, original, shieldEvent.get());
		}

		return shieldEvent.get().getBlockedDamage();
	}

	@Definition(id = "amount", local = @Local(type = float.class, ordinal = 0, argsOnly = true))
	@Expression("amount = @(0.0)")
	@ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
	private float modifyTotalDamage(float original, @Share("shieldEvent") LocalRef<LivingShieldBlockEvent> shieldEvent) {
		if (original != 0.0) {
			return shieldEvent.get().getDamageContainer().getConflictResolver().resolve(DamageContainer.DamageConflictType.SHIELD_DAMAGE, original, shieldEvent.get());
		}

		return shieldEvent.get().getDamageContainer().getNewDamage();
	}

	@Definition(id = "bl", local = @Local(type = boolean.class, ordinal = 0))
	@Expression("bl = @(true)")
	@ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkIsDamageAmountFullyBlocked(boolean original, @Local(argsOnly = true) float damage) {
		return original && damage <= 0;
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V"))
	private void updateContainerWithVanillaChanges(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		port_lib$damageContainers.peek().setNewDamage(amount); //update container with vanilla changes
	}

	@Inject(method = "hurt", at = {@At(value = "RETURN", ordinal = 4), @At(value = "RETURN", ordinal = 5)})
	private void popContainerFromStack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		port_lib$damageContainers.pop();
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", ordinal = 0))
	private void setContainerReductionByInvulnerability(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		port_lib$damageContainers.peek().setReduction(DamageContainer.Reduction.INVULNERABILITY, lastHurt);
	}

	@Definition(id = "invulnerableTime", field = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I")
	@Expression("this.invulnerableTime = @(20)")
	@ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int modifyPostAttackInvulnerabilityTicks(int original) {
		DamageContainer container = port_lib$damageContainers.peek();
		if (container.getPostAttackInvulnerabilityTicks() != 20) {
			return container.getPostAttackInvulnerabilityTicks();
		}

		return original;
	}

	@ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"), argsOnly = true)
	private float updateLocalAmountWithContainer(float original) {
		DamageContainer container = port_lib$damageContainers.peek();
		// TODO: I don't know if getOriginalDamage would be the same of the argument
//		if (original != container.getOriginalDamage()) {
//			return container.getConflictResolver().resolve(DamageContainer.DamageConflictType.NORMAL, original, null);
//		}

		return container.getNewDamage();
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

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 6), index = 2)
	private float livingDamageEvent(float value, DamageSource pDamageSource) {
		LivingDamageEvent event = new LivingDamageEvent((LivingEntity) (Object) this, pDamageSource, value);
		event.sendEvent();
		if (event.isCanceled())
			return 0;
		return event.getAmount();
	}

	@Definition(id = "ServerPlayer", type = ServerPlayer.class)
	@Expression("this instanceof ServerPlayer")
	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void addDamageReductionByMobEffect(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir, @Local(ordinal = 3) float resistedDamage) {
		port_lib$damageContainers.peek().setReduction(DamageContainer.Reduction.MOB_EFFECTS, resistedDamage);
	}

	@ModifyExpressionValue(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"))
	private float addDamageReductionByEnchantment(float original) {
		DamageContainer container = port_lib$damageContainers.peek();
		container.setReduction(DamageContainer.Reduction.ENCHANTMENTS, container.getNewDamage() - original);
		return original;
	}
}
