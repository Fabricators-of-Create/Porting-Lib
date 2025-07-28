package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.injections.IngredientInjection;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientInjection, FabricIngredient {
	@Shadow
	@Final
	private Ingredient.Value[] values;

	@Shadow
	public abstract ItemStack[] getItems();

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

	@Override
	public boolean port_lib$hasNoItems() {
		ItemStack[] items = getItems();
		if (items.length == 0)
			return true;
		if (items.length == 1) {
			// If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
			ItemStack item = items[0];
			return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
		}
		return false;
	}
}
