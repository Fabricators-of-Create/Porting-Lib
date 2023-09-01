package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@Shadow
	public abstract int guiWidth();

	@Shadow
	public abstract int guiHeight();

	@Unique
	private ItemStack port_lib$cachedStack = ItemStack.EMPTY;

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
	private void port_lib$cacheItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = itemStack;
	}

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
	private void port_lib$clearCachedItemStack(Font font, ItemStack itemStack, int i, int j, CallbackInfo ci) {
		port_lib$cachedStack = ItemStack.EMPTY;
	}

	@ModifyArgs(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"))
	private void port_lib$wrapTooltip(Args args, Font font, List<Component> lines, Optional<TooltipComponent> data, int x, int y) {
		if (ClientHooks.MODS_TO_WRAP.contains(BuiltInRegistries.ITEM.getKey(port_lib$cachedStack.getItem()).getNamespace())) {
			args.set(1, ClientHooks.gatherTooltipComponents(port_lib$cachedStack, lines, data, x, guiWidth(), guiHeight(), font));
		}
	}

	@ModifyArgs(method = "renderComponentTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"))
	private void port_lib$wrapTooltipComponent(Args args, Font font, List<Component> lines, int x, int y) {
		if (ClientHooks.MODS_TO_WRAP.contains(BuiltInRegistries.ITEM.getKey(port_lib$cachedStack.getItem()).getNamespace())) {
			args.set(1, ClientHooks.gatherTooltipComponents(port_lib$cachedStack, lines, Optional.empty(), x, guiWidth(), guiHeight(), font));
		}
	}
}
