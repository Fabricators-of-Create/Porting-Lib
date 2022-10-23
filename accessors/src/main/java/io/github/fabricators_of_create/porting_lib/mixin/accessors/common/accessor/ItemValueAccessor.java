package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.ItemValue.class)
public interface ItemValueAccessor {
	@Invoker("<init>")
	static Ingredient.ItemValue createItemValue(ItemStack itemStack) {
		throw new UnsupportedOperationException();
	}
}
