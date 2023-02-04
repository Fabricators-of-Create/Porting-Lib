package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.List;
import java.util.Optional;

import io.github.fabricators_of_create.porting_lib.event.client.PreRenderTooltipCallback;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTooltipBorderColorCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Shadow
	public int width;
	@Shadow
	public int height;
	@Shadow
	protected Font font;
	@Unique
	private ItemStack port_lib$cachedStack = ItemStack.EMPTY;

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.AFTER), cancellable = true)
	private void port_lib$preTooltipRender(PoseStack matrices, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		if (PreRenderTooltipCallback.EVENT.invoker().onPreRenderTooltip(port_lib$cachedStack, matrices, x, y, width, height, this.font, components))
			ci.cancel();
	}

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void port_lib$cacheItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = itemStack;
	}

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
	private void port_lib$clearCachedItemStack(PoseStack matrixStack, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = ItemStack.EMPTY;
	}

	@ModifyArgs(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V"))
	private void port_lib$wrapTooltip(Args args, PoseStack matrices, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
		if (ClientHooks.MODS_TO_WRAP.contains(Registry.ITEM.getKey(port_lib$cachedStack.getItem()).getNamespace())) {
			args.set(1, ClientHooks.gatherTooltipComponents(port_lib$cachedStack, lines, data, x, width, height, this.font));
		}
	}

	@ModifyArgs(method = "renderComponentTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V"))
	private void port_lib$wrapTooltipComponent(Args args, PoseStack matrices, List<Component> lines, int x, int y) {
		if (ClientHooks.MODS_TO_WRAP.contains(Registry.ITEM.getKey(port_lib$cachedStack.getItem()).getNamespace())) {
			args.set(1, ClientHooks.gatherTooltipComponents(port_lib$cachedStack, lines, Optional.empty(), x, width, height, this.font));
		}
	}

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lorg/joml/Matrix4f;"))
	private void port_lib$cacheBorderColors(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = RenderTooltipBorderColorCallback.EVENT.invoker()
				.onTooltipBorderColor(port_lib$cachedStack, ScreenHelper.DEFAULT_BORDER_COLOR_START, ScreenHelper.DEFAULT_BORDER_COLOR_END);
	}

	@Inject(method = "renderTooltipInternal", at = @At("RETURN"))
	private void port_lib$clearBorderColors(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = null;
	}
}
