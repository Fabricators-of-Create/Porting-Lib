package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.StackEntry.class)
public interface ItemValueAccessor {
	@Invoker("<init>")
	static Ingredient.StackEntry createItemValue(ItemStack itemStack) {
		throw new UnsupportedOperationException();
	}
}
