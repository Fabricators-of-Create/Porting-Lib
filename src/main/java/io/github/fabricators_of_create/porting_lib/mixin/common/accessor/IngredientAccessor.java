package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
	@Accessor("values")
	Ingredient.Value[] port_lib$getAcceptedItems();

	@Invoker("fromValues")
	static Ingredient port_lib$fromValues(Stream<? extends Ingredient.Value> stream) {
		throw new AssertionError("Mixin application failed!");
	}
}
