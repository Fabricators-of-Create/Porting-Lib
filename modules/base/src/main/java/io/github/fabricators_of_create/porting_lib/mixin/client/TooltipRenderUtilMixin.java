package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.util.ScreenHelper;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
	@ModifyExpressionValue(method = "renderTooltipBackground", at = @At(value = "CONSTANT", args = "intValue=1347420415"))
	private static int changeTooltipBorderStartColor(int original) {
		return ScreenHelper.CURRENT_COLOR == null ? original : ScreenHelper.CURRENT_COLOR.getBorderColorStart();
	}

	@ModifyExpressionValue(method = "renderTooltipBackground", at = @At(value = "CONSTANT", args = "intValue=1344798847"))
	private static int changeTooltipBorderEndColor(int original) {
		return ScreenHelper.CURRENT_COLOR == null ? original : ScreenHelper.CURRENT_COLOR.getBorderColorEnd();
	}
}
