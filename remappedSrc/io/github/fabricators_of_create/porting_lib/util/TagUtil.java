package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.extensions.TierExtensions;
import me.alphamode.forgetags.Tags;
import net.minecraft.block.Block;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tags.TagKey;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

public class TagUtil {
	@Nullable
	public static DyeColor getColorFromStack(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof DyeItem dyeItem) {
			return dyeItem.getColor();
		}

		for (DyeColor color : DyeColor.values()) {
			if (stack.isIn(color.getTag())) return color;
		}

		return null;
	}

	public static TagKey<Block> getTagFromVanillaTier(ToolMaterials tier) {
		return switch (tier) {
			case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
			case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
			case STONE -> BlockTags.NEEDS_STONE_TOOL;
			case IRON -> BlockTags.NEEDS_IRON_TOOL;
			case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
			case NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
		};
	}

	public static TagKey<Block> getTagFromTier(ToolMaterial tier) {
		return tier.getTag();
	}
}
