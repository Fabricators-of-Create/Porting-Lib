package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.item.extensions.ContinueUsingItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.EntitySwingListenerItem;

import io.github.fabricators_of_create.porting_lib.item.extensions.EquipmentItem;

import io.github.fabricators_of_create.porting_lib.item.extensions.ShieldBlockItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.UsingTickItem;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

	@Shadow
	@NotNull
	public abstract ItemStack getWeaponItem();

	public LivingEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
		ItemStack stack = getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof EntitySwingListenerItem listener && listener.onEntitySwing(stack, (LivingEntity) (Object) this))
			ci.cancel();
	}

	@Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
	private void getSlotForItemStack(ItemStack itemStack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (itemStack.getItem() instanceof EquipmentItem equipment) {
			EquipmentSlot slot = equipment.getEquipmentSlot(itemStack);

			if (slot != null) {
				cir.setReturnValue(slot);
			}
		}
	}

	@Inject(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = Shift.AFTER, ordinal = 1))
	public void onUsingTick(CallbackInfo ci) {
		if (useItem.getItem() instanceof UsingTickItem usingTickItem) {
			if (!this.useItem.isEmpty()) {
				if (useItemRemaining > 0)
					usingTickItem.onUsingTick(useItem, (LivingEntity) (Object) this, useItemRemaining);
			}
		}
	}

	@ModifyExpressionValue(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	public boolean canContinueUsing(boolean original) {
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

	@ModifyReturnValue(method = "canDisableShield", at = @At("RETURN"))
	private boolean canDisableShieldItem(boolean original) {
		if (!original) {
			ItemStack weapon = getWeaponItem();
			if (weapon.getItem() instanceof ShieldBlockItem shieldBlockItem)
				return shieldBlockItem.canDisableShield(weapon, this.useItem, (LivingEntity) (Object) this, (LivingEntity) (Object) this);
		}
		return original;
	}
}
