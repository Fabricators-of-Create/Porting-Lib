package io.github.fabricators_of_create.porting_lib.mixin.common;

import static io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil.HEIGHT;
import static io.github.fabricators_of_create.porting_lib.util.ShapedRecipeUtil.WIDTH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.item.crafting.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin {
	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 0)
	)
	private static int port_lib$modifyMaxHeight(int original) {
		return HEIGHT;
	}

	@ModifyConstant(
			method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(intValue = 3, ordinal = 1)
	)
	private static int port_lib$modifyMaxWidth(int original) {
		return WIDTH;
	}

	@ModifyConstant(method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(stringValue = "Invalid pattern: too many rows, 3 is maximum")
	)
	private static String port_lib$changeHeightWarning(String original) {
		return "Invalid pattern: too many rows, " + HEIGHT + " is maximum";
	}

	@ModifyConstant(method = "patternFromJson(Lcom/google/gson/JsonArray;)[Ljava/lang/String;",
			constant = @Constant(stringValue = "Invalid pattern: too many columns, 3 is maximum")
	)
	private static String port_lib$changeWidthWarning(String original) {
		return "Invalid pattern: too many columns, " + WIDTH + " is maximum";
	}
}
