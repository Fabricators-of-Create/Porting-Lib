package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.item.alchemy.PotionBrewing;

import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionBrewing.Mix.class)
public interface PotionBrewing$MixAccessor<T> {
	@Accessor("from")
	T port_lib$from();

	@Accessor("ingredient")
	Ingredient port_lib$ingredient();

	@Accessor("to")
	T port_lib$to();
}
