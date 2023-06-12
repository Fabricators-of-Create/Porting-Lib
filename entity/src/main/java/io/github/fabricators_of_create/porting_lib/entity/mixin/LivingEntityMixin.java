package io.github.fabricators_of_create.porting_lib.entity.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.ShieldBlockCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.ShieldBlockCallback.ShieldBlockEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents.FallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents.HurtEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityFinishUsingItemCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	protected int lastHurtByPlayerTime;

	@Shadow
	public abstract ItemStack getUseItem();

	@Shadow
	@Nullable
	protected Player lastHurtByPlayer;

	public LivingEntityMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	// drops and loot

	@ModifyArgs(
			method = "dropAllDeathLoot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;IZ)V"
			)
	)
	private void modifyLootingLevel(Args args) {
		DamageSource source = args.get(0);
		int originalLevel = args.get(1);
		boolean recentlyHit = args.get(2);
		int modifiedLevel = LivingEntityLootEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(
				source, (LivingEntity) (Object) this, originalLevel, recentlyHit
		);
		args.set(1, modifiedLevel);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void startCapturingDeathLoot(DamageSource damageSource, CallbackInfo ci) {
		startCapturingDrops();
	}

	@Unique
	private int lootingLevel;

	@ModifyVariable(
			method = "dropAllDeathLoot",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/LivingEntity;lastHurtByPlayerTime:I"
			)
	)
	private int grabLootingLevel(int lootingLevel) {
		this.lootingLevel = lootingLevel;
		return lootingLevel;
	}

	@Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
	private void dropCapturedDrops(DamageSource source, CallbackInfo ci) {
		List<ItemEntity> drops = finishCapturingDrops();
		boolean cancelled = LivingEntityLootEvents.DROPS.invoker().onLivingEntityDrops(
				(LivingEntity) (Object) this, source, drops, lootingLevel, lastHurtByPlayerTime > 0
		);
		if (!cancelled)
			drops.forEach(level()::addFreshEntity);
	}

	// shield blocking

	@ModifyExpressionValue(
			method = "hurt",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"
			)
	)
	private boolean fireShieldBlockEvent(boolean isBlocked,
							   DamageSource source, float amount,
							   @Share("ShieldBlockEvent") LocalRef<ShieldBlockEvent> sharedEvent) {
		if (!isBlocked)
			return false;
		ShieldBlockEvent event = new ShieldBlockEvent((LivingEntity) (Object) this, getUseItem(), source, amount);
		sharedEvent.set(event); // save to check if the shield gets damaged later
		ShieldBlockCallback.EVENT.invoker().onShieldBlock(event);
		return !event.isCancelled();
	}

	@WrapWithCondition(
			method = "hurt",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
			)
	)
	private boolean checkHurtShield(LivingEntity self, float shieldDamage,
									DamageSource source, float amount,
									@Share("ShieldBlockEvent") LocalRef<ShieldBlockEvent> sharedEvent) {
		return sharedEvent.get().damageShield;
	}

	@ModifyVariable(
			method = "hurt",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
					)
			),
			at = @At(value = "STORE", ordinal = 0),
			ordinal = 2 // floats: amount, f, g
	)
	private float modifyBlockedAmount(float originalStored,
									 DamageSource source, float amount,
									 @Share("ShieldBlockEvent") LocalRef<ShieldBlockEvent> sharedEvent) {
		return sharedEvent.get().damageBlocked;
	}

	@ModifyExpressionValue(
			method = "hurt",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
					)
			),
			at = @At(value = "CONSTANT",args = "floatValue=0")
	)
	private float modifyDamage(float newDamage, // should be 0
							   DamageSource source, float amount,
							   @Share("ShieldBlockEvent") LocalRef<ShieldBlockEvent> sharedEvent) {
		return amount - sharedEvent.get().damageBlocked;
	}

	// fall event

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void fireFallEvent(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir,
							  @Share("FallEvent") LocalRef<FallEvent> sharedEvent) {
		FallEvent event = new FallEvent((LivingEntity) (Object) this, source, fallDistance, multiplier);
		sharedEvent.set(event);
		LivingEntityDamageEvents.FALL.invoker().onFall(event);
		if (event.isCancelled())
			cir.setReturnValue(false);
	}

	@ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD", shift = Shift.AFTER), argsOnly = true, ordinal = 0)
	public float modifyFallDistance(float fallDistance,
									@Share("FallEvent") LocalRef<FallEvent> sharedEvent) {
		return sharedEvent.get().distance;
	}

	@ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD", shift = Shift.AFTER), argsOnly = true, ordinal = 1)
	public float modifyFallDamageMultiplier(float multiplier,
											@Share("FallEvent") LocalRef<FallEvent> sharedEvent) {
		return sharedEvent.get().damageMultiplier;
	}

	// hurt event

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void fireHurtEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir,
							   @Share("HurtEvent") LocalRef<HurtEvent> sharedEvent) {
		HurtEvent event = new HurtEvent((LivingEntity) (Object) this, source, amount);
		sharedEvent.set(event);
		LivingEntityDamageEvents.HURT.invoker().onHurt(event);
		if (event.isCancelled())
			cir.setReturnValue(false);
	}

	@ModifyVariable(method = "hurt", at = @At(value = "HEAD", shift = Shift.AFTER), argsOnly = true)
	private float modifyDamageAmount(float amount, DamageSource source, float amount2,
									 @Share("HurtEvent") LocalRef<HurtEvent> sharedEvent) {
		return sharedEvent.get().damageAmount;
	}

	// misc events

	@ModifyArg(
			method = "dropExperience",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
			)
	)
	private int modifyExperienceRewardAmount(int amount) {
		return LivingEntityLootEvents.EXPERIENCE_DROP.invoker().onLivingEntityExperienceDrop(
				amount, lastHurtByPlayer, (LivingEntity) (Object) this
		);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		LivingEntityEvents.TICK.invoker().onLivingEntityTick((LivingEntity) (Object) this);
	}

	@ModifyVariable(method = "knockback", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private double modifyKnockbackStrength(double strength) {
		return LivingEntityDamageEvents.KNOCKBACK_STRENGTH.invoker().onKnockback((LivingEntity) (Object) this, strength);
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void onJump(CallbackInfo ci) {
		LivingEntityEvents.JUMP.invoker().onLivingEntityJump((LivingEntity) (Object) this);
	}

	@WrapOperation(
			method = "completeUsingItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
			)
	)
	public ItemStack onItemUseFinish(ItemStack used, Level level, LivingEntity self, Operation<ItemStack> original) {
		ItemStack copy = used.copy();
		ItemStack result = original.call(used, level, self);
		ItemStack modified = LivingEntityFinishUsingItemCallback.EVENT.invoker().modifyUseResult(
				(LivingEntity) (Object) this, copy, result
		);
		return modified != null ? modified : result;
	}
}
