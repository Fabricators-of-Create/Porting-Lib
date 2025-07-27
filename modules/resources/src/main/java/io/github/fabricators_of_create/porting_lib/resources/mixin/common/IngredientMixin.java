package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.injections.IngredientInjection;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public class IngredientMixin implements IngredientInjection, FabricIngredient {
	@Shadow
	@Final
	private Ingredient.Value[] values;

	@Override
	public Ingredient.Value[] port_lib$getValues() {
		if (port_lib$isCustom()) {
			throw new IllegalStateException("Cannot retrieve values from custom ingredient!");
		}
		return this.values;
	}

	@Override
	public boolean port_lib$isCustom() {
		return getCustomIngredient() != null;
	}
}
