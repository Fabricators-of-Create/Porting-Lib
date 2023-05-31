package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.common.PotionEvents;
import io.github.fabricators_of_create.porting_lib.item.ContinueUsingItem;
import io.github.fabricators_of_create.porting_lib.item.EntitySwingListenerItem;
import io.github.fabricators_of_create.porting_lib.item.EquipmentItem;
import io.github.fabricators_of_create.porting_lib.item.UsingTickItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("ConstantConditions")
@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand interactionHand);

	@Shadow
	protected ItemStack useItem;

	@Shadow
	protected int useItemRemaining;

	@Shadow
	public abstract InteractionHand getUsedItemHand();

	public LivingEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}


	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void port_lib$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
		ItemStack stack = getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof EntitySwingListenerItem listener && listener.onEntitySwing(stack, (LivingEntity) (Object) this))
			ci.cancel();
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
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
	protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos,
								   CallbackInfo ci, @Local(index = 16) int count) {
		if (state.getBlock() instanceof CustomLandingEffectsBlock custom &&
				custom.addLandingEffects(state, (ServerLevel) level(), pos, state, (LivingEntity) (Object) this, count)) {
			super.checkFallDamage(y, onGround, state, pos);
			ci.cancel();
		}
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(
			method = "travel",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")),
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	public float port_lib$setSlipperiness(float p) {
		BlockPos pos = getBlockPosBelowThatAffectsMyMovement();
		BlockState state = level().getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level(), pos, (LivingEntity) (Object) this);
		}
		return p;
	}

	@Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
	private static void port_lib$getSlotForItemStack(ItemStack itemStack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (itemStack.getItem() instanceof EquipmentItem equipment) {
			cir.setReturnValue(equipment.getEquipmentSlot(itemStack));
		}
	}

	@Inject(
			method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
					shift = Shift.BY,
					by = 3,
					remap = false
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$addEffect(MobEffectInstance newEffect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir,
								   MobEffectInstance oldEffect) {
		PotionEvents.POTION_ADDED.invoker().onPotionAdded((LivingEntity) (Object) this, newEffect, oldEffect, source);
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	public void port_lib$canBeAffected(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		InteractionResult result = PotionEvents.POTION_APPLICABLE.invoker().onPotionApplicable((LivingEntity) (Object) this, effect);
		if (result != InteractionResult.PASS)
			cir.setReturnValue(result == InteractionResult.SUCCESS);
	}

	@Inject(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = Shift.AFTER, ordinal = 1))
	public void port_lib$onUsingTick(CallbackInfo ci) {
		if (useItem.getItem() instanceof UsingTickItem usingTickItem) {
			if (!this.useItem.isEmpty()) {
				if (useItemRemaining > 0)
					usingTickItem.onUsingTick(useItem, (LivingEntity) (Object) this, useItemRemaining);
			}
		}
	}

	@ModifyExpressionValue(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	public boolean port_lib$canContinueUsing(boolean original) {
		if (useItem.getItem() instanceof ContinueUsingItem continueUsingItem) {
			ItemStack to = this.getItemInHand(this.getUsedItemHand());
			if (!useItem.isEmpty() && !to.isEmpty())
			{
				return continueUsingItem.canContinueUsing(useItem, to);
			}
			return false;
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "handleOnClimbable",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
			)
	)
	private boolean customScaffoldingMovement(boolean original) {
		BlockState state = getFeetBlockState();
		if (state.getBlock() instanceof CustomScaffoldingBlock custom)
			return custom.isScaffolding(state, level, blockPosition(), (LivingEntity) (Object) this);
		return original;
	}
}
