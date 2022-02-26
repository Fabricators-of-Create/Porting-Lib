package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {
	@Accessor
	static List<Ingredient> getALLOWED_CONTAINERS() {
		throw new UnsupportedOperationException();
	}
}
