package io.github.fabricators_of_create.porting_lib.brewing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrewingHandler {
	public static void doBrew(Level level, BlockPos blockPos, NonNullList<ItemStack> inputs, int[] inputIndexes) {
		ItemStack itemstack = inputs.get(3);

		BrewingRecipeRegistry.brewPotions(inputs, itemstack, inputIndexes);
		if (itemstack.getItem().hasCraftingRemainingItem()) {
			ItemStack itemstack1 = itemstack.getItem().getCraftingRemainingItem().getDefaultInstance();
			itemstack.shrink(1);
			if (itemstack.isEmpty()) {
				itemstack = itemstack1;
			} else {
				Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemstack1);
			}
		}
		else itemstack.shrink(1);

		inputs.set(3, itemstack);
		level.levelEvent(1035, blockPos, 0);
	}
}
