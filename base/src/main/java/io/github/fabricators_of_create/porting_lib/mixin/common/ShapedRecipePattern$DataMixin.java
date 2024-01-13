package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.item.crafting.ShapedRecipePattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapedRecipePattern.Data.class)
public class ShapedRecipePattern$DataMixin {
	@ModifyExpressionValue(
			method = "method_55096", // comapFlatMap lambda in codec
			at = @At(value = "CONSTANT", args = "intValue=3")
	)
	private static int removeSizeLimits(int original) {
		return Integer.MAX_VALUE;
	}
}
