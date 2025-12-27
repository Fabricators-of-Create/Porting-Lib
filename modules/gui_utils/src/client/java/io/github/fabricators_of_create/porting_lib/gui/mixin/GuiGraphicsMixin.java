package io.github.fabricators_of_create.porting_lib.gui.mixin;

import io.github.fabricators_of_create.porting_lib.gui.GuiHooks;
import io.github.fabricators_of_create.porting_lib.gui.extensions.GuiGraphicsExtension;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements GuiGraphicsExtension {

	@Shadow
	public abstract int guiWidth();

	@Shadow
	public abstract int guiHeight();

	@Shadow
	protected abstract void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner);

	private ItemStack port_lib$tooltipStack = ItemStack.EMPTY;

	@Override
	public void renderComponentTooltip(Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
		this.port_lib$tooltipStack = stack;
		List<ClientTooltipComponent> components = GuiHooks.gatherTooltipComponents(stack, tooltips, mouseX, guiWidth(), guiHeight(), font);
		this.renderTooltipInternal(font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
		this.port_lib$tooltipStack = ItemStack.EMPTY;
	}

	@ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
	private void port_lib$wrapTooltip(Args args, Font font, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
		if (GuiHooks.MODS_TO_WRAP.contains(BuiltInRegistries.ITEM.getKey(port_lib$tooltipStack.getItem()).getNamespace())) {
			args.set(1, GuiHooks.gatherTooltipComponents(port_lib$tooltipStack, lines, data, x, guiWidth(), guiHeight(), font));
		}
	}

	@ModifyArgs(method = "renderComponentTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"))
	private void port_lib$wrapTooltipComponent(Args args, Font font, List<Component> lines, int x, int y) {
		if (GuiHooks.MODS_TO_WRAP.contains(BuiltInRegistries.ITEM.getKey(port_lib$tooltipStack.getItem()).getNamespace())) {
			args.set(1, GuiHooks.gatherTooltipComponents(port_lib$tooltipStack, lines, Optional.empty(), x, guiWidth(), guiHeight(), font));
		}
	}
}
