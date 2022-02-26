package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil;

import net.minecraft.world.item.crafting.ShapelessRecipe;

@Mixin(ShapelessRecipe.Serializer.class)
public abstract class ShapelessRecipe$SerializerMixin {
	@ModifyConstant(
			method = "fromJson(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonObject;)Lnet/minecraft/world/item/crafting/ShapelessRecipe;",
			constant = @Constant(intValue = 9)
	)
	private static int port_lib$modifyMaxItemsInRecipe(int original) {
		return ShapedRecipeUtil.HEIGHT * ShapedRecipeUtil.WIDTH;
	}
}
