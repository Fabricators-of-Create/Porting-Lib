package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.alchemy.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {
	@Accessor("CONTAINER_MIXES")
	static List<PotionBrewing.Mix<Item>> port_lib$CONTAINER_MIXES() {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor("POTION_MIXES")
	static List<PotionBrewing.Mix<Potion>> port_lib$POTION_MIXES() {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor("ALLOWED_CONTAINERS")
	static List<Ingredient> port_lib$ALLOWED_CONTAINERS() {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor("ALLOWED_CONTAINER")
	static Predicate<ItemStack> port_lib$ALLOWED_CONTAINER() {
		throw new RuntimeException("mixin failed!");
	}
}
