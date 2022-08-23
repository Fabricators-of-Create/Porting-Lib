package me.alphamode.forgetags.data;

import static me.alphamode.forgetags.Tags.Blocks.BARRELS;
import static me.alphamode.forgetags.Tags.Blocks.BARRELS_WOODEN;
import static me.alphamode.forgetags.Tags.Blocks.CHESTS;
import static me.alphamode.forgetags.Tags.Blocks.CHESTS_ENDER;
import static me.alphamode.forgetags.Tags.Blocks.CHESTS_TRAPPED;
import static me.alphamode.forgetags.Tags.Blocks.CHESTS_WOODEN;
import static me.alphamode.forgetags.Tags.Blocks.COBBLESTONE;
import static me.alphamode.forgetags.Tags.Blocks.COBBLESTONE_DEEPSLATE;
import static me.alphamode.forgetags.Tags.Blocks.COBBLESTONE_INFESTED;
import static me.alphamode.forgetags.Tags.Blocks.COBBLESTONE_MOSSY;
import static me.alphamode.forgetags.Tags.Blocks.COBBLESTONE_NORMAL;
import static me.alphamode.forgetags.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST;
import static me.alphamode.forgetags.Tags.Blocks.END_STONES;
import static me.alphamode.forgetags.Tags.Blocks.FENCES;
import static me.alphamode.forgetags.Tags.Blocks.FENCES_NETHER_BRICK;
import static me.alphamode.forgetags.Tags.Blocks.FENCES_WOODEN;
import static me.alphamode.forgetags.Tags.Blocks.FENCE_GATES;
import static me.alphamode.forgetags.Tags.Blocks.FENCE_GATES_WOODEN;
import static me.alphamode.forgetags.Tags.Blocks.GLASS;
import static me.alphamode.forgetags.Tags.Blocks.GLASS_COLORLESS;
import static me.alphamode.forgetags.Tags.Blocks.GLASS_PANES;
import static me.alphamode.forgetags.Tags.Blocks.GLASS_PANES_COLORLESS;
import static me.alphamode.forgetags.Tags.Blocks.GLASS_SILICA;
import static me.alphamode.forgetags.Tags.Blocks.GLASS_TINTED;
import static me.alphamode.forgetags.Tags.Blocks.GRAVEL;
import static me.alphamode.forgetags.Tags.Blocks.NETHERRACK;
import static me.alphamode.forgetags.Tags.Blocks.OBSIDIAN;
import static me.alphamode.forgetags.Tags.Blocks.ORES;
import static me.alphamode.forgetags.Tags.Blocks.ORES_COAL;
import static me.alphamode.forgetags.Tags.Blocks.ORES_COPPER;
import static me.alphamode.forgetags.Tags.Blocks.ORES_DIAMOND;
import static me.alphamode.forgetags.Tags.Blocks.ORES_EMERALD;
import static me.alphamode.forgetags.Tags.Blocks.ORES_GOLD;
import static me.alphamode.forgetags.Tags.Blocks.ORES_IN_GROUND_DEEPSLATE;
import static me.alphamode.forgetags.Tags.Blocks.ORES_IN_GROUND_NETHERRACK;
import static me.alphamode.forgetags.Tags.Blocks.ORES_IN_GROUND_STONE;
import static me.alphamode.forgetags.Tags.Blocks.ORES_IRON;
import static me.alphamode.forgetags.Tags.Blocks.ORES_LAPIS;
import static me.alphamode.forgetags.Tags.Blocks.ORES_NETHERITE_SCRAP;
import static me.alphamode.forgetags.Tags.Blocks.ORES_QUARTZ;
import static me.alphamode.forgetags.Tags.Blocks.ORES_REDSTONE;
import static me.alphamode.forgetags.Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE;
import static me.alphamode.forgetags.Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK;
import static me.alphamode.forgetags.Tags.Blocks.ORE_BEARING_GROUND_STONE;
import static me.alphamode.forgetags.Tags.Blocks.ORE_RATES_DENSE;
import static me.alphamode.forgetags.Tags.Blocks.ORE_RATES_SINGULAR;
import static me.alphamode.forgetags.Tags.Blocks.ORE_RATES_SPARSE;
import static me.alphamode.forgetags.Tags.Blocks.SAND;
import static me.alphamode.forgetags.Tags.Blocks.SANDSTONE;
import static me.alphamode.forgetags.Tags.Blocks.SAND_COLORLESS;
import static me.alphamode.forgetags.Tags.Blocks.SAND_RED;
import static me.alphamode.forgetags.Tags.Blocks.STAINED_GLASS;
import static me.alphamode.forgetags.Tags.Blocks.STAINED_GLASS_PANES;
import static me.alphamode.forgetags.Tags.Blocks.STONE;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_AMETHYST;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_COAL;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_COPPER;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_DIAMOND;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_EMERALD;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_GOLD;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_IRON;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_LAPIS;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_NETHERITE;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_QUARTZ;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_RAW_IRON;
import static me.alphamode.forgetags.Tags.Blocks.STORAGE_BLOCKS_REDSTONE;

import java.util.Locale;
import java.util.function.Consumer;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public BlockTagProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		tag(BARRELS).addTag(BARRELS_WOODEN);
		tag(BARRELS_WOODEN).add(Blocks.BARREL);
		tag(CHESTS).addTag(CHESTS_ENDER).addTag(CHESTS_TRAPPED).addTag(CHESTS_WOODEN);
		tag(CHESTS_ENDER).add(Blocks.ENDER_CHEST);
		tag(CHESTS_TRAPPED).add(Blocks.TRAPPED_CHEST);
		tag(CHESTS_WOODEN).add(Blocks.CHEST, Blocks.TRAPPED_CHEST);
		tag(COBBLESTONE).addTag(COBBLESTONE_NORMAL).addTag(COBBLESTONE_INFESTED).addTag(COBBLESTONE_MOSSY).addTag(COBBLESTONE_DEEPSLATE);
		tag(COBBLESTONE_NORMAL).add(Blocks.COBBLESTONE);
		tag(COBBLESTONE_INFESTED).add(Blocks.INFESTED_COBBLESTONE);
		tag(COBBLESTONE_MOSSY).add(Blocks.MOSSY_COBBLESTONE);
		tag(COBBLESTONE_DEEPSLATE).add(Blocks.COBBLED_DEEPSLATE);
		tag(END_STONES).add(Blocks.END_STONE);
		tag(ENDERMAN_PLACE_ON_BLACKLIST);
		tag(FENCE_GATES).addTag(FENCE_GATES_WOODEN);
		tag(FENCE_GATES_WOODEN).add(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.CRIMSON_FENCE_GATE, Blocks.WARPED_FENCE_GATE);
		tag(FENCES).addTag(FENCES_NETHER_BRICK).addTag(FENCES_WOODEN);
		tag(FENCES_NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE);
		getOrCreateTagBuilder(FENCES_WOODEN).forceAddTag(BlockTags.WOODEN_FENCES);
		tag(GLASS).addTag(GLASS_COLORLESS).addTag(STAINED_GLASS).addTag(GLASS_TINTED);
		tag(GLASS_COLORLESS).add(Blocks.GLASS);
		tag(GLASS_SILICA).add(Blocks.GLASS, Blocks.BLACK_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS);
		tag(GLASS_TINTED).add(Blocks.TINTED_GLASS);
		addColored(tag(STAINED_GLASS)::add, GLASS, "{color}_stained_glass");
		tag(GLASS_PANES).addTag(GLASS_PANES_COLORLESS).addTag(STAINED_GLASS_PANES);
		tag(GLASS_PANES_COLORLESS).add(Blocks.GLASS_PANE);
		addColored(tag(STAINED_GLASS_PANES)::add, GLASS_PANES, "{color}_stained_glass_pane");
		tag(GRAVEL).add(Blocks.GRAVEL);
		tag(NETHERRACK).add(Blocks.NETHERRACK);
		tag(OBSIDIAN).add(Blocks.OBSIDIAN);
		tag(ORE_BEARING_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE);
		tag(ORE_BEARING_GROUND_NETHERRACK).add(Blocks.NETHERRACK);
		tag(ORE_BEARING_GROUND_STONE).add(Blocks.STONE);
		tag(ORE_RATES_DENSE).add(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
		tag(ORE_RATES_SINGULAR).add(Blocks.ANCIENT_DEBRIS, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.NETHER_QUARTZ_ORE);
		tag(ORE_RATES_SPARSE).add(Blocks.NETHER_GOLD_ORE);
		tag(ORES).addTag(ORES_COAL).addTag(ORES_COPPER).addTag(ORES_DIAMOND).addTag(ORES_EMERALD).addTag(ORES_GOLD).addTag(ORES_IRON).addTag(ORES_LAPIS).addTag(ORES_REDSTONE).addTag(ORES_QUARTZ).addTag(ORES_NETHERITE_SCRAP);
		getOrCreateTagBuilder(ORES_COAL).forceAddTag(BlockTags.COAL_ORES);
		getOrCreateTagBuilder(ORES_COPPER).forceAddTag(BlockTags.COPPER_ORES);
		getOrCreateTagBuilder(ORES_DIAMOND).forceAddTag(BlockTags.DIAMOND_ORES);
		getOrCreateTagBuilder(ORES_EMERALD).forceAddTag(BlockTags.EMERALD_ORES);
		getOrCreateTagBuilder(ORES_GOLD).forceAddTag(BlockTags.GOLD_ORES);
		getOrCreateTagBuilder(ORES_IRON).forceAddTag(BlockTags.IRON_ORES);
		getOrCreateTagBuilder(ORES_LAPIS).forceAddTag(BlockTags.LAPIS_ORES);
		tag(ORES_QUARTZ).add(Blocks.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(ORES_REDSTONE).forceAddTag(BlockTags.REDSTONE_ORES);
		tag(ORES_NETHERITE_SCRAP).add(Blocks.ANCIENT_DEBRIS);
		tag(ORES_IN_GROUND_DEEPSLATE).add(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
		tag(ORES_IN_GROUND_NETHERRACK).add(Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE);
		tag(ORES_IN_GROUND_STONE).add(Blocks.COAL_ORE, Blocks.COPPER_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE);
		tag(SAND).addTag(SAND_COLORLESS).addTag(SAND_RED);
		tag(SAND_COLORLESS).add(Blocks.SAND);
		tag(SAND_RED).add(Blocks.RED_SAND);
		tag(SANDSTONE).add(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE);
		tag(STONE).add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE, Blocks.DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.INFESTED_DEEPSLATE, Blocks.TUFF);
		tag(STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_AMETHYST).addTag(STORAGE_BLOCKS_COAL).addTag(STORAGE_BLOCKS_COPPER).addTag(STORAGE_BLOCKS_DIAMOND).addTag(STORAGE_BLOCKS_EMERALD).addTag(STORAGE_BLOCKS_GOLD).addTag(STORAGE_BLOCKS_IRON).addTag(STORAGE_BLOCKS_LAPIS).addTag(STORAGE_BLOCKS_QUARTZ).addTag(STORAGE_BLOCKS_RAW_COPPER).addTag(STORAGE_BLOCKS_RAW_GOLD).addTag(STORAGE_BLOCKS_RAW_IRON).addTag(STORAGE_BLOCKS_REDSTONE).addTag(STORAGE_BLOCKS_NETHERITE);
		tag(STORAGE_BLOCKS_AMETHYST).add(Blocks.AMETHYST_BLOCK);
		tag(STORAGE_BLOCKS_COAL).add(Blocks.COAL_BLOCK);
		tag(STORAGE_BLOCKS_COPPER).add(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER);
		tag(STORAGE_BLOCKS_DIAMOND).add(Blocks.DIAMOND_BLOCK);
		tag(STORAGE_BLOCKS_EMERALD).add(Blocks.EMERALD_BLOCK);
		tag(STORAGE_BLOCKS_GOLD).add(Blocks.GOLD_BLOCK);
		tag(STORAGE_BLOCKS_IRON).add(Blocks.IRON_BLOCK);
		tag(STORAGE_BLOCKS_LAPIS).add(Blocks.LAPIS_BLOCK);
		tag(STORAGE_BLOCKS_QUARTZ).add(Blocks.QUARTZ_BLOCK);
		tag(STORAGE_BLOCKS_RAW_COPPER).add(Blocks.RAW_COPPER_BLOCK);
		tag(STORAGE_BLOCKS_RAW_GOLD).add(Blocks.RAW_GOLD_BLOCK);
		tag(STORAGE_BLOCKS_RAW_IRON).add(Blocks.RAW_IRON_BLOCK);
		tag(STORAGE_BLOCKS_REDSTONE).add(Blocks.REDSTONE_BLOCK);
		tag(STORAGE_BLOCKS_NETHERITE).add(Blocks.NETHERITE_BLOCK);
	}

	private void addColored(Consumer<Block> consumer, TagKey<Block> group, String pattern) {
		String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
		for (DyeColor color : DyeColor.values()) {
			ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getName()));
			TagKey<Block> tag = getForgeTag(prefix + color.getName());
			Block block = Registry.BLOCK.get(key);
			if (block == null || block == Blocks.AIR)
				throw new IllegalStateException("Unknown vanilla block: " + key.toString());
			tag(tag).add(block);
			consumer.accept(block);
		}
	}

	@SuppressWarnings("unchecked")
	private TagKey<Block> getForgeTag(String name) {
		try {
			name = name.toUpperCase(Locale.ENGLISH);
			return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
		}
	}


	public FabricTagBuilder<Block> tag(TagKey<Block> tag) {
		return getOrCreateTagBuilder(tag);
	}
}
