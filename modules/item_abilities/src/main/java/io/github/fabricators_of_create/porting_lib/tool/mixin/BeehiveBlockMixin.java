package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
	@ModifyExpressionValue(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
	private boolean supportsToolAction(boolean original, @NotNull ItemStack stack) {
		if (stack.getItem() instanceof ItemAbilityItem toolActionItem)
			return toolActionItem.canPerformAction(stack, ItemAbilities.SHEARS_HARVEST);
		return original;
	}
}
