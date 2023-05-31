package io.github.fabricators_of_create.porting_lib.mixin.client;

import net.minecraft.client.gui.GuiGraphics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.util.ScreenHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public abstract class GuiComponentMixin {
	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V",
			at = @At("HEAD"),
			index = 6,
			argsOnly = true
	)
	private static int port_lib$replaceA(int a) {
		return port_lib$getColor(a);
	}

	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V",
			at = @At("HEAD"),
			index = 7,
			argsOnly = true
	)
	private static int port_lib$replaceB(int b) {
		return port_lib$getColor(b);
	}

	private static int port_lib$getColor(int original) {
		if (ScreenHelper.CURRENT_COLOR != null) {
			if (original == ScreenHelper.DEFAULT_BORDER_COLOR_START) {
				return ScreenHelper.CURRENT_COLOR.getBorderColorStart();
			} else if (original == ScreenHelper.DEFAULT_BORDER_COLOR_END) {
				return ScreenHelper.CURRENT_COLOR.getBorderColorEnd();
			}
		}
		return original;
	}
}
