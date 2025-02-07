package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tags.TagKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.TagEntry.class)
public interface TagValueAccessor {
	@Invoker("<init>")
	static Ingredient.TagEntry createTagValue(TagKey<Item> tag) {
		throw new UnsupportedOperationException();
	}
}
