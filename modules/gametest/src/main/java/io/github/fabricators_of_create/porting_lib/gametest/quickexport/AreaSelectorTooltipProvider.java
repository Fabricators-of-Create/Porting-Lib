package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import java.util.List;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public enum AreaSelectorTooltipProvider implements ItemTooltipCallback {
	INSTANCE;

	@Override
	public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipType, List<Component> lines) {
		if (!stack.is(PortingLibGameTest.AREA_SELECTOR))
			return;
		AreaSelection area = AreaSelectorItem.getArea(stack);
		Component tooltip;

		if (area == null) {
			tooltip = lang("no", ChatFormatting.RED);
		} else if (area.second == null) {
			tooltip = lang("partial", ChatFormatting.GREEN, formatPos(area.first));
		} else {
			BoundingBox box = area.asBoundingBox();
			BlockPos min = new BlockPos(box.minX(), box.minY(), box.minZ());
			BlockPos max = new BlockPos(box.maxX(), box.maxY(), box.maxZ());
			tooltip = lang("complete", ChatFormatting.GREEN, formatPos(min), formatPos(max));
		}

		lines.add(1, tooltip);
	}

	private static Component lang(String selectionType, ChatFormatting color, Object... args) {
		String key = "area_selector.tooltip." + selectionType + "_selection";
		return Component.translatable(key, args).withStyle(color);
	}

	private static String formatPos(BlockPos pos) {
		return "[%s, %s, %s]".formatted(pos.getX(), pos.getY(), pos.getZ());
	}
}
