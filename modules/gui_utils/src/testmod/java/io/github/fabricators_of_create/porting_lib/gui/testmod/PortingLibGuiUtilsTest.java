package io.github.fabricators_of_create.porting_lib.gui.testmod;

import java.util.List;
import java.util.Optional;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.gui.layered.GuiLayerRegistry;
import io.github.fabricators_of_create.porting_lib.gui.map.MapDecorationRendererManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class PortingLibGuiUtilsTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		GuiLayerRegistry.registerAboveAll(PortingLib.id("test_above_all"), ((guiGraphics, deltaTracker) -> {
			Minecraft mc = Minecraft.getInstance();
			guiGraphics.drawCenteredString(mc.font, "test above all", mc.getWindow().getWidth() / 2, mc.getWindow().getHeight() / 2, 0xFFFFFF);
		}));

		GuiLayerRegistry.registerBelowAll(PortingLib.id("test_below_all"), ((guiGraphics, deltaTracker) -> {
			Minecraft mc = Minecraft.getInstance();
			guiGraphics.drawCenteredString(mc.font, "test below all", mc.getWindow().getWidth() / 2, mc.getWindow().getHeight() / 2, 0xFFFFFF);
		}));

		MapDecorationType customType = new MapDecorationType(PortingLib.id("test_decoration"), true, FastColor.ARGB32.color(255, 255, 0, 0), true, true);
		MapDecorationRendererManager.register(customType, (decoration, poseStack, bufferSource, mapData, decorationTextures, inItemFrame, packedLight, index) -> {
			var font = Minecraft.getInstance().font;
			var width = font.width("Test Decoration");
			var scale = 25f / width;
			poseStack.pushPose();
			poseStack.translate(0.0F + decoration.x() / 2.0F + 64.0F - width * scale / 2.0F, 0.0F + decoration.y() / 2.0F + 64.0F + 4.0F, -0.025F);
			poseStack.scale(scale, scale, 1.0F);
			poseStack.translate(0.0F, 0.0F, -0.1F);
			font.drawInBatch("Test Decoration", 0f, 0f, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, packedLight);
			poseStack.popPose();

			return true;
		});

		ClientTickEvents.END_WORLD_TICK.register(level -> {
			var player = Minecraft.getInstance().player;
			var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (stack.getItem() instanceof MapItem) {
				var data = MapItem.getSavedData(stack, level);
				data.addClientSideDecorations(List.of(new MapDecoration(Holder.direct(customType), (byte) 0, (byte) 0, (byte) 0, Optional.empty())));
			}
		});
	}
}
