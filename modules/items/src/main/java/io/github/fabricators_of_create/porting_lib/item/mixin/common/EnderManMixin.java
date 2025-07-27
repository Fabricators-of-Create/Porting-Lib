package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.EnderMaskItem;
import net.minecraft.world.entity.monster.EnderMan;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
	@WrapOperation(method = "isLookingAtMe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
	private boolean port_lib$checkIsEndermanMask(ItemStack instance, Item item, Operation<Boolean> original, @Local(argsOnly = true) Player player) {
		if (instance.getItem() instanceof EnderMaskItem enderMaskItem) {
			return enderMaskItem.isEnderMask(instance, player, (EnderMan) (Object) this);
		}

		return original.call(instance, item);
	}
}
