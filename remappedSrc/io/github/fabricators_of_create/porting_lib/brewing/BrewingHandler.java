package io.github.fabricators_of_create.porting_lib.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrewingHandler {
	public static void doBrew(World level, BlockPos blockPos, DefaultedList<ItemStack> inputs, int[] inputIndexes) {
		ItemStack itemstack = inputs.get(3);

		BrewingRecipeRegistry.brewPotions(inputs, itemstack, inputIndexes);
		if (itemstack.getItem().hasRecipeRemainder()) {
			ItemStack itemstack1 = itemstack.getItem().getRecipeRemainder().getDefaultStack();
			itemstack.decrement(1);
			if (itemstack.isEmpty()) {
				itemstack = itemstack1;
			} else {
				ItemScatterer.spawn(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemstack1);
			}
		}
		else itemstack.decrement(1);

		inputs.set(3, itemstack);
		level.syncWorldEvent(1035, blockPos, 0);
	}
}
