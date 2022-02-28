package io.github.fabricators_of_create.porting_lib.util;

import static me.alphamode.forgetags.Tags.Items.DYES_BLACK;
import static me.alphamode.forgetags.Tags.Items.DYES_BLUE;
import static me.alphamode.forgetags.Tags.Items.DYES_BROWN;
import static me.alphamode.forgetags.Tags.Items.DYES_CYAN;
import static me.alphamode.forgetags.Tags.Items.DYES_GRAY;
import static me.alphamode.forgetags.Tags.Items.DYES_GREEN;
import static me.alphamode.forgetags.Tags.Items.DYES_LIGHT_BLUE;
import static me.alphamode.forgetags.Tags.Items.DYES_LIGHT_GRAY;
import static me.alphamode.forgetags.Tags.Items.DYES_LIME;
import static me.alphamode.forgetags.Tags.Items.DYES_MAGENTA;
import static me.alphamode.forgetags.Tags.Items.DYES_ORANGE;
import static me.alphamode.forgetags.Tags.Items.DYES_PINK;
import static me.alphamode.forgetags.Tags.Items.DYES_PURPLE;
import static me.alphamode.forgetags.Tags.Items.DYES_RED;
import static me.alphamode.forgetags.Tags.Items.DYES_WHITE;
import static me.alphamode.forgetags.Tags.Items.DYES_YELLOW;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TierExtensions;
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

		if (DYES_BLACK.contains(item)) return DyeColor.BLACK;
		if (DYES_BLUE.contains(item)) return DyeColor.BLUE;
		if (DYES_BROWN.contains(item)) return DyeColor.BROWN;
		if (DYES_CYAN.contains(item)) return DyeColor.CYAN;
		if (DYES_GRAY.contains(item)) return DyeColor.GRAY;
		if (DYES_GREEN.contains(item)) return DyeColor.GREEN;
		if (DYES_LIGHT_BLUE.contains(item)) return DyeColor.LIGHT_BLUE;
		if (DYES_LIGHT_GRAY.contains(item)) return DyeColor.LIGHT_GRAY;
		if (DYES_LIME.contains(item)) return DyeColor.LIME;
		if (DYES_MAGENTA.contains(item)) return DyeColor.MAGENTA;
		if (DYES_ORANGE.contains(item)) return DyeColor.ORANGE;
		if (DYES_PINK.contains(item)) return DyeColor.PINK;
		if (DYES_PURPLE.contains(item)) return DyeColor.PURPLE;
		if (DYES_RED.contains(item)) return DyeColor.RED;
		if (DYES_WHITE.contains(item)) return DyeColor.WHITE;
		if (DYES_YELLOW.contains(item)) return DyeColor.YELLOW;

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
		return ((TierExtensions) tier).getTag();
	}
}
