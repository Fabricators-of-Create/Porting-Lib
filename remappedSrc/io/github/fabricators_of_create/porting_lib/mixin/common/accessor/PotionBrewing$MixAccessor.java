package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingRecipeRegistry.Recipe.class)
public interface PotionBrewing$MixAccessor<T> {
	@Accessor("from")
	T port_lib$from();

	@Accessor("ingredient")
	Ingredient port_lib$ingredient();

	@Accessor("to")
	T port_lib$to();
}
