package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public enum AreaSelectionRenderer implements AfterEntities {
	INSTANCE;

	private static final double tinyOffset = 0.01; // z fighting

	@Override
	public void afterEntities(WorldRenderContext context) {
		LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = player.getItemInHand(hand);
			if (!held.is(PortingLibGameTest.AREA_SELECTOR))
				continue;

			AreaSelection area = AreaSelectorItem.getArea(held);
			PoseStack poseStack = context.matrixStack();
			MultiBufferSource buffers = Objects.requireNonNull(context.consumers());
			Camera camera = context.camera();

			if (area == null) { // nothing selected, render selection
				renderLookTarget(poseStack, buffers, camera, player);
			} else {
				BlockPos second = area.second != null ? area.second : AreaSelectorItem.getLookTarget(player);
				BoundingBox box = BoundingBox.fromCorners(area.first, second);
				renderBox(poseStack, buffers, camera, box);
			}
		}
	}

	public static void renderLookTarget(PoseStack poseStack, MultiBufferSource buffers, Camera camera, Player player) {
		BlockPos pos = AreaSelectorItem.getLookTarget(player);
		renderBox(poseStack, buffers, camera, new BoundingBox(pos));
	}

	public static void renderBox(PoseStack poseStack, MultiBufferSource buffers, Camera camera, BoundingBox box) {
		poseStack.pushPose();

		Vec3 camPos = camera.getPosition();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
		poseStack.translate(box.minX(), box.minY(), box.minZ());

		VertexConsumer consumer = buffers.getBuffer(RenderType.lines());
		LevelRenderer.renderLineBox(
				poseStack,
				consumer,
				-tinyOffset, -tinyOffset, -tinyOffset,
				box.getXSpan() + tinyOffset, box.getYSpan() + tinyOffset, box.getZSpan() + tinyOffset,
				1, 1, 1, 1
		);
		poseStack.popPose();
	}


}
