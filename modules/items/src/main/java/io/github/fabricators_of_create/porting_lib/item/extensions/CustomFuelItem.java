package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.RecipeType;

import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

public interface CustomFuelItem {
	/**
	 * @return The fuel burn time for this item stack in a furnace. Return 0 to make
	 *         it not act as a fuel. Return -1 to let the default vanilla logic decide.
	 * @apiNote You should try to use {@link FuelRegistry#add} where possible, as this method is primarily for items that require
	 * 			the usage of {@link RecipeType}.
	 */
	default int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		Integer fuel = FuelRegistry.INSTANCE.get((ItemLike) this);
		return fuel == null ? 0 : fuel;
	}
}
