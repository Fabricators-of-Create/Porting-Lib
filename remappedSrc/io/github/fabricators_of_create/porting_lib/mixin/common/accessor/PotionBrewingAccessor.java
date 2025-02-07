package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingRecipeRegistry.class)
public interface PotionBrewingAccessor {
	@Accessor("CONTAINER_MIXES")
	static List<BrewingRecipeRegistry.Recipe<Item>> port_lib$CONTAINER_MIXES() {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor("POTION_MIXES")
	static List<BrewingRecipeRegistry.Recipe<Potion>> port_lib$POTION_MIXES() {
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
