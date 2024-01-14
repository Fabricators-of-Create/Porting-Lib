package io.github.fabricators_of_create.porting_lib.item.impl.mixin.client;

import io.github.fabricators_of_create.porting_lib.item.impl.client.ItemDecoratorHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
	@Inject(
			method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.AFTER)
	)
	private void renderCustomItemDecorations(Font textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
		ItemDecoratorHandler.of(stack).render((GuiGraphics) (Object) this, textRenderer, stack, x, y);
	}
}
