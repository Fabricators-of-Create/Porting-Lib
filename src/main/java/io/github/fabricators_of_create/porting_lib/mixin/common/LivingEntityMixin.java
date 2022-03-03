package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.ArrayList;
import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.item.EntitySwingListenerItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	protected Player lastHurtByPlayer;

	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand interactionHand);

	@Unique
	private DamageSource mantle$currentDamageSource;

	public LivingEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void mantle$spawnDropsHEAD(DamageSource source, CallbackInfo ci) {
		mantle$currentDamageSource = source;
		this.captureDrops(new ArrayList<>());
	}

	@ModifyVariable(method = "dropAllDeathLoot",
			at = @At(value = "STORE", ordinal = 0))
	private int mantle$spawnDropsBODY1(int j) {
		int modifiedLevel = LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(mantle$currentDamageSource, (LivingEntity) (Object) this, j);
		if (modifiedLevel != 0) {
			return modifiedLevel;
		} else {
			return EnchantmentHelper.getMobLooting((LivingEntity) mantle$currentDamageSource.getEntity());
		}
	}

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void onFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		LivingEntityEvents.LivingFallEvent event = new LivingEntityEvents.LivingFallEvent((LivingEntity) (Object) this, fallDistance, multiplier);
		event.sendEvent();
		if(event.isCanceled())
			cir.setReturnValue(false);
		if(fallDistance != event.getDistance())
			fallDistance = event.getDistance();
		if(multiplier != event.getDamageMultiplier())
			multiplier = event.getDamageMultiplier();
	}

	@ModifyVariable(method = "dropAllDeathLoot",
			at = @At(value = "STORE", ordinal = 1))
	private int mantle$spawnDropsBODY2(int j) {
		return LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(mantle$currentDamageSource, (LivingEntity) (Object) this, j);
	}

	@ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private void mantle$onHurt(Args args) {
		DamageSource source = args.get(0);
		float currentAmount = args.get(1);
		float newAmount = LivingEntityEvents.ACTUALLY_HURT.invoker().onHurt(source, (LivingEntity) (Object) this, currentAmount);
		if (newAmount != currentAmount)
			args.set(1, newAmount);
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void mantle$spawnDropsTAIL(DamageSource source, CallbackInfo ci) {
		Collection<ItemEntity> drops = this.captureDrops(null);
		if (!LivingEntityEvents.DROPS.invoker().onLivingEntityDrops((LivingEntity) (Object) this, source, drops))
			drops.forEach(e -> level.addFreshEntity(e));
	}

	@Environment(EnvType.CLIENT)
	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void mantle$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
		ItemStack stack = getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof EntitySwingListenerItem && ((EntitySwingListenerItem) stack.getItem())
				.onEntitySwing(stack, (LivingEntity) (Object) this)) { ci.cancel();	}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private void mantle$tick(CallbackInfo ci) {
		LivingEntityEvents.TICK.invoker().onLivingEntityTick((LivingEntity) (Object) this);
	}

	@ModifyVariable(method = "knockback", at = @At("STORE"), ordinal = 0)
	private double mantle$takeKnockback(double f) {
		if (lastHurtByPlayer != null)
			return LivingEntityEvents.KNOCKBACK_STRENGTH.invoker().onLivingEntityTakeKnockback(f, lastHurtByPlayer);

		return f;
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float mantle$onHurt(float amount, DamageSource source, float amount2) {
		return LivingEntityEvents.HURT.invoker().onHurt(source, amount);
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void mantle$onJump(CallbackInfo ci) {
		LivingEntityEvents.JUMP.invoker().onLivingEntityJump((LivingEntity) (Object) this);
	}

	@Inject(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	protected void mantle$updateFallState(double d, boolean bl, BlockState blockState, BlockPos blockPos,
										  CallbackInfo ci, float f, double e, int i) {
		if (blockState.addLandingEffects((ServerLevel) level, blockPos, blockState, MixinHelper.cast(this), i)) {
			super.checkFallDamage(d, bl, blockState, blockPos);
			ci.cancel();
		}
	}

	// TODO Make this less :concern: when fabric's mixin fork updates
	@ModifyVariable(method = "travel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"), index = 7)
	public float mantle$setSlipperiness(float t) {
		return MixinHelper.<LivingEntity>cast(this).level.getBlockState(getBlockPosBelowThatAffectsMyMovement())
				.getSlipperiness(MixinHelper.<LivingEntity>cast(this).level, getBlockPosBelowThatAffectsMyMovement(), MixinHelper.<LivingEntity>cast(this));
	}

	//Moved from MobMixin
	@Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
	private static void mantle$getSlotForItemStack(ItemStack itemStack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (itemStack.getItem() instanceof EquipmentItem equipment) {
			cir.setReturnValue(equipment.getEquipmentSlot(itemStack));
		}
	}

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "JUMP", opcode = Opcodes.IFNONNULL), locals = LocalCapture.CAPTURE_FAILHARD)
	public void mantle$onPotionEffectAdded(MobEffectInstance oldEffectInstance, Entity source, CallbackInfoReturnable<Boolean> cir, MobEffectInstance newEffectInstance) {
		PotionEvents.POTION_ADDED.invoker().onPotionAdded(new PotionEvents.PotionAddedEvent(MixinHelper.cast(this), oldEffectInstance, newEffectInstance, source));
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	public void mantle$potionApplicable(MobEffectInstance potioneffect, CallbackInfoReturnable<Boolean> cir) {
		PotionEvents.PotionApplicableEvent event = new PotionEvents.PotionApplicableEvent(MixinHelper.cast(this), potioneffect);
		event.sendEvent();
		if(event.getResult() != InteractionResult.PASS)
			cir.setReturnValue(event.getResult() == InteractionResult.SUCCESS);
	}
}
