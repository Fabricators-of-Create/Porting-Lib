package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.crafting.ShapelessRecipe;

@Mixin(ShapelessRecipe.Serializer.class)
public abstract class ShapelessRecipe$SerializerMixin {
	@ModifyExpressionValue(
			method = "method_53760", // flatXmap lambda in codec
			at = @At(value = "CONSTANT", args = "intValue=9")
	)
	private static int removeItemLimit(int original) {
		return Integer.MAX_VALUE;
	}
}
