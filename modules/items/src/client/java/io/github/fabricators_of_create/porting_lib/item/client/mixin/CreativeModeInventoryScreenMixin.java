package io.github.fabricators_of_create.porting_lib.item.client.mixin;

import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.gui.components.EditBox;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.item.extensions.CreativeModeTabExt;
import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen implements FabricCreativeInventoryScreen {
	@Shadow
	private EditBox searchBox;

	public CreativeModeInventoryScreenMixin(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@ModifyArg(method = "renderLabels", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V"))
	private int modifyLabelColor(int labelColor) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) getSelectedItemGroup()).getPortingTabData();
		if (data != null)
			return data.labelColor();
		return labelColor;
	}

	@ModifyArg(method = "renderBg", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private Identifier modifyScrollerSprite(Identifier identifier) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) getSelectedItemGroup()).getPortingTabData();
		if (data != null)
			return data.scrollerSprite();
		return identifier;
	}

	@Definition(id = "SEARCH", field = "Lnet/minecraft/world/item/CreativeModeTab$Type;SEARCH:Lnet/minecraft/world/item/CreativeModeTab$Type;")
	@Expression("? == SEARCH")
	@ModifyExpressionValue(method = {"refreshCurrentTabContents", "selectTab", "getTooltipFromContainerItem"}, at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean hasSearchBar(boolean original) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) getSelectedItemGroup()).getPortingTabData();
		if (data != null)
			return data.hasSearchBar();
		return original;
	}

	@Definition(id = "SEARCH", field = "Lnet/minecraft/world/item/CreativeModeTab$Type;SEARCH:Lnet/minecraft/world/item/CreativeModeTab$Type;")
	@Expression("? != SEARCH")
	@ModifyExpressionValue(method = {"getTooltipFromContainerItem", "charTyped", "keyPressed"}, at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean doesntHaveSearchBar(boolean original) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) getSelectedItemGroup()).getPortingTabData();
		if (data != null)
			return data.hasSearchBar();
		return original;
	}

	@Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;refreshSearchResults()V"))
	private void setSearchBarWidth(CreativeModeTab tab, CallbackInfo ci) {
		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) getSelectedItemGroup()).getPortingTabData();
		if (data != null) {
			this.searchBox.setWidth(data.searchBarWidth());
			this.searchBox.setX(this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth());
		}
	}

//	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
//	private ResourceLocation modifyTabImage(ResourceLocation original) { TODO: I don't think this is needed anymore neo forge doesn't even use the method anymore...
//		PortingLibCreativeTab.TabData data = ((CreativeModeTabExt) selectedTab).getPortingTabData();
//		if (data != null)
//			return data.tabsImage();
//		return original;
//	}
}
