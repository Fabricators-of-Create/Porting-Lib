package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.inventory.AnvilMenu;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@Unique
	private boolean port_lib$enchantedBook = false;
	@Unique
	private ItemStack port_lib$inputStack = null;
	@Unique
	private ItemStack port_lib$bookStack = null;
	@Unique // default: not a custom item
	private TriState port_lib$canEnchant = TriState.DEFAULT;

	@ModifyVariable(method = "createResult", at = @At(value = "STORE", target = "Lnet/minecraft/nbt/ListTag;isEmpty()Z"))
	private boolean port_lib$grabEnchantedState(boolean bl) {
		this.port_lib$enchantedBook = bl;
		return bl;
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"
			)
	)
	private ItemStack port_lib$grabInputStack(ItemStack stack) {
		this.port_lib$inputStack = stack;
		return stack;
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;",
					ordinal = 1
			)
	)
	private ItemStack port_lib$grabBookStack(ItemStack stack) {
		this.port_lib$bookStack = stack;
		return stack;
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/inventory/DataSlot;get()I",
					ordinal = 1
			)
	)
	private int port_lib$checkEnchantability$cost(int cost) {
		if (port_lib$enchantedBook) {
			ItemStack stack = port_lib$inputStack;
			ItemStack book = port_lib$bookStack;
			if (stack != null && book != null && stack.getItem() instanceof CustomEnchantingBehaviorItem custom) {
				port_lib$canEnchant = TriState.of(custom.isBookEnchantable(stack, book));
			}
		}
		// reset when done
		port_lib$enchantedBook = false;

		// original: get() > 40
		if (port_lib$canEnchant.orElse(true)) {
			// can enchant or not a custom item - let vanilla check run
			return cost;
		} else {
			// can't enchant. Make the statement true and force a reset.
			return Integer.MAX_VALUE;
		}
	}

	@ModifyExpressionValue(
			method = "createResult",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z",
					ordinal = 1
			)
	)
	private boolean port_lib$checkEnchantability$instabuild(boolean instabuild) {
		// original: !instabuild
		// if we can enchant, let vanilla behavior run.
		if (port_lib$canEnchant == TriState.FALSE) {
			// can't enchant. We want to force true so the statement runs, but it's inverted, so  set it to false.
			instabuild = false;
		}
		// reset when done
		port_lib$canEnchant = TriState.DEFAULT;
		return instabuild;
	}
}
