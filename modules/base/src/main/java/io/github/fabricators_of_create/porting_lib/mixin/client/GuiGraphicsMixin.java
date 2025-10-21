package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.List;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.GuiGraphics;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.event.client.PreRenderTooltipCallback;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTooltipBorderColorCallback;
import io.github.fabricators_of_create.porting_lib.util.ScreenHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@Shadow
	@Final
	private PoseStack pose;

	@Shadow
	public abstract int guiWidth();

	@Shadow
	public abstract int guiHeight();

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.AFTER), cancellable = true)
	private void port_lib$preTooltipRender(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		if (PreRenderTooltipCallback.EVENT.invoker().onPreRenderTooltip(ScreenHelper.currentTooltipStack(), this.pose, x, y, guiWidth(), guiHeight(), font, components))
			ci.cancel();
	}

	@WrapMethod(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
	private void grabStackForTooltip(Font font, ItemStack stack, int x, int y, Operation<Void> original) {
		ScreenHelper.withTooltipStack(stack, () -> original.call(font, stack, x, y));
	}

	@WrapOperation(
			method = "renderTooltipInternal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"
			)
	)
	private void modifyTooltipBorderColors(GuiGraphics self, Runnable runnable, Operation<Void> original) {
		ScreenHelper.CURRENT_COLOR = RenderTooltipBorderColorCallback.EVENT.invoker().onTooltipBorderColor(
				ScreenHelper.currentTooltipStack(), ScreenHelper.DEFAULT_BORDER_COLOR_START, ScreenHelper.DEFAULT_BORDER_COLOR_END
		);

		original.call(self, runnable);

		ScreenHelper.CURRENT_COLOR = null;
	}
}
