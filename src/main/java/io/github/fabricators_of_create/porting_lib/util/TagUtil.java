package io.github.fabricators_of_create.porting_lib.util;

import javax.annotation.Nullable;

import me.alphamode.forgetags.DyeUtil;
import me.alphamode.forgetags.Tags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;

public class TagUtil {
	@Nullable
	public static DyeColor getColorFromStack(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof DyeItem dyeItem) {
			return dyeItem.getDyeColor();
		}

		for (DyeColor color : DyeColor.values()) {
			if (DyeUtil.getDyeTag(color).contains(stack.getItem())) {
				return color;
			}
		}

		return null;
	}

	public static Tag.Named<Block> getTagFromVanillaTier(Tiers tier) {
		return switch (tier) {
			case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
			case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
			case STONE -> BlockTags.NEEDS_STONE_TOOL;
			case IRON -> BlockTags.NEEDS_IRON_TOOL;
			case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
			case NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
		};
	}

	public static Tag<Block> getTagFromTier(Tier tier) {
		return tier.getTag();
	}
}
