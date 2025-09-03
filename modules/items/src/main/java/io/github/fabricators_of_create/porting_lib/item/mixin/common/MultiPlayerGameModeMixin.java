package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.item.extensions.BlockBreakResetItem;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@WrapOperation(method = "sameDestroyTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean checkShouldResetBlockBreak(ItemStack stack, ItemStack other, Operation<Boolean> original) {
		if (other.getItem() instanceof BlockBreakResetItem blockBreakResetItem) {
			return !blockBreakResetItem.shouldCauseBlockBreakReset(other, stack);
		}

		return original.call(stack, other);
	}
}
