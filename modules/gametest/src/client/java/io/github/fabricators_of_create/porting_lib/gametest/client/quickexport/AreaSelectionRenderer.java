package io.github.fabricators_of_create.porting_lib.gametest.client.quickexport;

import java.util.Objects;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelection;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelectorItem;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.StartMain;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldTerrainRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.CommonColors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public enum AreaSelectionRenderer implements StartMain {
	INSTANCE;

	private static final double tinyOffset = 0.01; // z fighting

	@Override
	public void startMain(WorldTerrainRenderContext context) {
		LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack held = player.getItemInHand(hand);
			if (!held.is(PortingLibGameTest.AREA_SELECTOR))
				continue;

			AreaSelection area = AreaSelectorItem.getArea(held);

			if (area == null) { // nothing selected, render selection
				renderLookTarget(player);
			} else {
				BlockPos second = area.second != null ? area.second : AreaSelectorItem.getLookTarget(player);
				BoundingBox box = BoundingBox.fromCorners(area.first, second);
				renderBox(box);
			}
		}
	}

	public static void renderLookTarget(Player player) {
		BlockPos pos = AreaSelectorItem.getLookTarget(player);
		renderBox(new BoundingBox(pos));
	}

	public static void renderBox(BoundingBox box) {
		Gizmos.cuboid(AABB.of(box), GizmoStyle.stroke(CommonColors.WHITE));
	}


}
