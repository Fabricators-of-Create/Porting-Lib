package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.TagValue.class)
public interface TagValueAccessor {
	@Invoker("<init>")
	static Ingredient.TagValue createTagValue(TagKey<Item> tag) {
		throw new UnsupportedOperationException();
	}
}
