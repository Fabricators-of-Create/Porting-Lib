package io.github.fabricators_of_create.porting_lib.item.impl.mixin.client;

import io.github.fabricators_of_create.porting_lib.item.api.extensions.CreativeModeTabExt;
import io.github.fabricators_of_create.porting_lib.item.api.itemgroup.PortingLibCreativeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {
	@Shadow
	private static CreativeModeTab selectedTab;

	@ModifyArg(method = "renderLabels", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
	private int modifyLabelColor(int labelColor) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) selectedTab).getPortingTabData();
		if (data != null)
			return data.labelColor();
		return labelColor;
	}

//	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
//	private ResourceLocation modifyTabImage(ResourceLocation original) { TODO: I don't think this is needed anymore neo forge doesn't even use the method anymore...
//		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) selectedTab).getPortingTabData();
//		if (data != null)
//			return data.tabsImage();
//		return original;
//	}
}
