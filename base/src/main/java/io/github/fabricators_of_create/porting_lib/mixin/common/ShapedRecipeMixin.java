package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.item.crafting.ShapedRecipe;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {
	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 0)
	)
	private static int port_lib$modifyMaxHeight(int original) {
		return ShapedRecipeUtil.HEIGHT;
	}

	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 1)
	)
	private static int port_lib$modifyMaxWidth(int original) {
		return ShapedRecipeUtil.WIDTH;
	}

	@ModifyConstant(method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(stringValue = "Invalid pattern: too many rows, 3 is maximum")
	)
	private static String port_lib$changeHeightWarning(String original) {
		return "Invalid pattern: too many rows, " + ShapedRecipeUtil.HEIGHT + " is maximum";
	}

	@ModifyConstant(method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(stringValue = "Invalid pattern: too many columns, 3 is maximum")
	)
	private static String port_lib$changeWidthWarning(String original) {
		return "Invalid pattern: too many columns, " + ShapedRecipeUtil.WIDTH + " is maximum";
	}

	@Inject(method = "itemStackFromJson", at = @At("HEAD"), cancellable = true)
	private static void port_lib$customNbtItemStack(JsonObject json, CallbackInfoReturnable<ItemStack> cir) {
		if (json.has("nbt"))
			cir.setReturnValue(CraftingHelper.getItemStack(json, true, true));
	}
}
