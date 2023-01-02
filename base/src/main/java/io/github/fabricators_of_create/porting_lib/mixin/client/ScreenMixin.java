package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.List;

import io.github.fabricators_of_create.porting_lib.util.ScreenHelper;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTooltipBorderColorCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

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
	private void port_lib$preTooltipRender(PoseStack matrices, List<ClientTooltipComponent> components, int x, int y, CallbackInfo ci) {
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

	@Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lcom/mojang/math/Matrix4f;"))
	private void port_lib$cacheBorderColors(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = RenderTooltipBorderColorCallback.EVENT.invoker()
				.onTooltipBorderColor(port_lib$cachedStack, ScreenHelper.DEFAULT_BORDER_COLOR_START, ScreenHelper.DEFAULT_BORDER_COLOR_END);
	}

	@Inject(method = "renderTooltipInternal", at = @At("RETURN"))
	private void port_lib$clearBorderColors(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
		ScreenHelper.CURRENT_COLOR = null;
	}
}
