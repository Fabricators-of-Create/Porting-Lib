package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.PreRenderTooltipCallback;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderTooltipBorderColorCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

	@Unique
	private ItemStack port_lib$cachedStack = ItemStack.EMPTY;

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.AFTER), cancellable = true)
	private void port_lib$preTooltipRender(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		if (PreRenderTooltipCallback.EVENT.invoker().onPreRenderTooltip(port_lib$cachedStack, this.pose, x, y, guiWidth(), guiHeight(), font, components))
			ci.cancel();
	}

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void port_lib$cacheItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = itemStack;
	}

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
	private void port_lib$clearCachedItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = ItemStack.EMPTY;
	}

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lorg/joml/Matrix4f;"))
	private void port_lib$cacheBorderColors(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		RenderTooltipBorderColorCallback.BorderColorEntry.CURRENT_COLOR = RenderTooltipBorderColorCallback.EVENT.invoker()
				.onTooltipBorderColor(port_lib$cachedStack, RenderTooltipBorderColorCallback.DEFAULT_BORDER_COLOR_START, RenderTooltipBorderColorCallback.DEFAULT_BORDER_COLOR_END);
	}

	@Inject(method = "renderTooltipInternal", at = @At("RETURN"))
	private void port_lib$clearBorderColors(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		RenderTooltipBorderColorCallback.BorderColorEntry.CURRENT_COLOR = null;
	}

	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V",
			at = @At("HEAD"),
			index = 4,
			argsOnly = true
	)
	private int replaceA(int a) {
		return port_lib$getColor(a);
	}

	@ModifyVariable(
			method = "fillGradient(Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIIIII)V",
			at = @At("HEAD"),
			index = 5,
			argsOnly = true
	)
	private int replaceB(int b) {
		return port_lib$getColor(b);
	}

	private static int port_lib$getColor(int original) {
		if (RenderTooltipBorderColorCallback.BorderColorEntry.CURRENT_COLOR != null) {
			if (original == RenderTooltipBorderColorCallback.DEFAULT_BORDER_COLOR_START) {
				return RenderTooltipBorderColorCallback.BorderColorEntry.CURRENT_COLOR.getBorderColorStart();
			} else if (original == RenderTooltipBorderColorCallback.DEFAULT_BORDER_COLOR_END) {
				return RenderTooltipBorderColorCallback.BorderColorEntry.CURRENT_COLOR.getBorderColorEnd();
			}
		}
		return original;
	}
}
