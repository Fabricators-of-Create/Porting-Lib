package io.github.fabricators_of_create.porting_lib.tags.data;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, BlockTagProvider blockTags) {
		super(output, registriesFuture, blockTags);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		copy(Tags.Blocks.BARRELS, Tags.Items.BARRELS);
		copy(Tags.Blocks.BARRELS_WOODEN, Tags.Items.BARRELS_WOODEN);
		tag(Tags.Items.BONES).add(Items.BONE);
		copy(Tags.Blocks.BOOKSHELVES, Tags.Items.BOOKSHELVES);
		copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
		copy(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
		copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
		copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
		copy(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
		copy(Tags.Blocks.COBBLESTONE_NORMAL, Tags.Items.COBBLESTONE_NORMAL);
		copy(Tags.Blocks.COBBLESTONE_INFESTED, Tags.Items.COBBLESTONE_INFESTED);
		copy(Tags.Blocks.COBBLESTONE_MOSSY, Tags.Items.COBBLESTONE_MOSSY);
		copy(Tags.Blocks.COBBLESTONE_DEEPSLATE, Tags.Items.COBBLESTONE_DEEPSLATE);
		tag(Tags.Items.CROPS).addTag(Tags.Items.CROPS_BEETROOT).addTag(Tags.Items.CROPS_CARROT).addTag(Tags.Items.CROPS_NETHER_WART).addTag(Tags.Items.CROPS_POTATO).addTag(Tags.Items.CROPS_WHEAT);
		tag(Tags.Items.CROPS_BEETROOT).add(Items.BEETROOT);
		tag(Tags.Items.CROPS_CARROT).add(Items.CARROT);
		tag(Tags.Items.CROPS_NETHER_WART).add(Items.NETHER_WART);
		tag(Tags.Items.CROPS_POTATO).add(Items.POTATO);
		tag(Tags.Items.CROPS_WHEAT).add(Items.WHEAT);
		tag(Tags.Items.DUSTS).addTag(Tags.Items.DUSTS_GLOWSTONE).addTag(Tags.Items.DUSTS_PRISMARINE).addTag(Tags.Items.DUSTS_REDSTONE);
		tag(Tags.Items.DUSTS_GLOWSTONE).add(Items.GLOWSTONE_DUST);
		tag(Tags.Items.DUSTS_PRISMARINE).add(Items.PRISMARINE_SHARD);
		tag(Tags.Items.DUSTS_REDSTONE).add(Items.REDSTONE);
		addColored(tag(Tags.Items.DYES)::addTag, Tags.Items.DYES, "{color}_dye");
		tag(Tags.Items.EGGS).add(Items.EGG);
		tag(Tags.Items.ENCHANTING_FUELS).addTag(Tags.Items.GEMS_LAPIS);
		copy(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
		tag(Tags.Items.ENDER_PEARLS).add(Items.ENDER_PEARL);
		tag(Tags.Items.FEATHERS).add(Items.FEATHER);
		copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
		copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
		copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
		copy(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
		copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
		tag(Tags.Items.GEMS).addTag(Tags.Items.GEMS_AMETHYST).addTag(Tags.Items.GEMS_DIAMOND).addTag(Tags.Items.GEMS_EMERALD).addTag(Tags.Items.GEMS_LAPIS).addTag(Tags.Items.GEMS_PRISMARINE).addTag(Tags.Items.GEMS_QUARTZ);
		tag(Tags.Items.GEMS_AMETHYST).add(Items.AMETHYST_SHARD);
		tag(Tags.Items.GEMS_DIAMOND).add(Items.DIAMOND);
		tag(Tags.Items.GEMS_EMERALD).add(Items.EMERALD);
		tag(Tags.Items.GEMS_LAPIS).add(Items.LAPIS_LAZULI);
		tag(Tags.Items.GEMS_PRISMARINE).add(Items.PRISMARINE_CRYSTALS);
		tag(Tags.Items.GEMS_QUARTZ).add(Items.QUARTZ);
		copy(Tags.Blocks.GLASS, Tags.Items.GLASS);
		copy(Tags.Blocks.GLASS_TINTED, Tags.Items.GLASS_TINTED);
		copy(Tags.Blocks.GLASS_SILICA, Tags.Items.GLASS_SILICA);
		copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
		copy(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
		copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
		copy(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
		tag(Tags.Items.GUNPOWDER).add(Items.GUNPOWDER);
		tag(Tags.Items.HEADS).add(Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.ZOMBIE_HEAD);
		tag(Tags.Items.INGOTS).addTag(Tags.Items.INGOTS_BRICK).addTag(Tags.Items.INGOTS_COPPER).addTag(Tags.Items.INGOTS_GOLD).addTag(Tags.Items.INGOTS_IRON).addTag(Tags.Items.INGOTS_NETHERITE).addTag(Tags.Items.INGOTS_NETHER_BRICK);
		tag(Tags.Items.INGOTS_BRICK).add(Items.BRICK);
		tag(Tags.Items.INGOTS_COPPER).add(Items.COPPER_INGOT);
		tag(Tags.Items.INGOTS_GOLD).add(Items.GOLD_INGOT);
		tag(Tags.Items.INGOTS_IRON).add(Items.IRON_INGOT);
		tag(Tags.Items.INGOTS_NETHERITE).add(Items.NETHERITE_INGOT);
		tag(Tags.Items.INGOTS_NETHER_BRICK).add(Items.NETHER_BRICK);
		tag(Tags.Items.LEATHER).add(Items.LEATHER);
		tag(Tags.Items.MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM);
		tag(Tags.Items.NETHER_STARS).add(Items.NETHER_STAR);
		copy(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
		tag(Tags.Items.NUGGETS).addTag(Tags.Items.NUGGETS_IRON).addTag(Tags.Items.NUGGETS_GOLD);
		tag(Tags.Items.NUGGETS_IRON).add(Items.IRON_NUGGET);
		tag(Tags.Items.NUGGETS_GOLD).add(Items.GOLD_NUGGET);
		copy(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
		copy(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE, Tags.Items.ORE_BEARING_GROUND_DEEPSLATE);
		copy(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK, Tags.Items.ORE_BEARING_GROUND_NETHERRACK);
		copy(Tags.Blocks.ORE_BEARING_GROUND_STONE, Tags.Items.ORE_BEARING_GROUND_STONE);
		copy(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
		copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
		copy(Tags.Blocks.ORE_RATES_SPARSE, Tags.Items.ORE_RATES_SPARSE);
		copy(Tags.Blocks.ORES, Tags.Items.ORES);
		copy(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
		copy(Tags.Blocks.ORES_COPPER, Tags.Items.ORES_COPPER);
		copy(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
		copy(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
		copy(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
		copy(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
		copy(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
		copy(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
		copy(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
		copy(Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Items.ORES_NETHERITE_SCRAP);
		copy(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE, Tags.Items.ORES_IN_GROUND_DEEPSLATE);
		copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
		copy(Tags.Blocks.ORES_IN_GROUND_STONE, Tags.Items.ORES_IN_GROUND_STONE);
		tag(Tags.Items.RAW_MATERIALS).addTag(Tags.Items.RAW_MATERIALS_COPPER).addTag(Tags.Items.RAW_MATERIALS_GOLD).addTag(Tags.Items.RAW_MATERIALS_IRON);
		tag(Tags.Items.RAW_MATERIALS_COPPER).add(Items.RAW_COPPER);
		tag(Tags.Items.RAW_MATERIALS_GOLD).add(Items.RAW_GOLD);
		tag(Tags.Items.RAW_MATERIALS_IRON).add(Items.RAW_IRON);
		tag(Tags.Items.RODS).addTag(Tags.Items.RODS_BLAZE).addTag(Tags.Items.RODS_WOODEN);
		tag(Tags.Items.RODS_BLAZE).add(Items.BLAZE_ROD);
		tag(Tags.Items.RODS_WOODEN).add(Items.STICK);
		copy(Tags.Blocks.SAND, Tags.Items.SAND);
		copy(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
		copy(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
		copy(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
		tag(Tags.Items.SEEDS).addTag(Tags.Items.SEEDS_BEETROOT).addTag(Tags.Items.SEEDS_MELON).addTag(Tags.Items.SEEDS_PUMPKIN).addTag(Tags.Items.SEEDS_WHEAT);
		tag(Tags.Items.SEEDS_BEETROOT).add(Items.BEETROOT_SEEDS);
		tag(Tags.Items.SEEDS_MELON).add(Items.MELON_SEEDS);
		tag(Tags.Items.SEEDS_PUMPKIN).add(Items.PUMPKIN_SEEDS);
		tag(Tags.Items.SEEDS_WHEAT).add(Items.WHEAT_SEEDS);
		tag(Tags.Items.SHEARS).add(Items.SHEARS);
		tag(Tags.Items.SLIMEBALLS).add(Items.SLIME_BALL);
		copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
		copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
		copy(Tags.Blocks.STONE, Tags.Items.STONE);
		copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
		copy(Tags.Blocks.STORAGE_BLOCKS_AMETHYST, Tags.Items.STORAGE_BLOCKS_AMETHYST);
		copy(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
		copy(Tags.Blocks.STORAGE_BLOCKS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER);
		copy(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
		copy(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
		copy(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
		copy(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
		copy(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
		copy(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
		copy(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
		copy(Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER);
		copy(Tags.Blocks.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD);
		copy(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON);
		copy(Tags.Blocks.STORAGE_BLOCKS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE);
		tag(Tags.Items.STRING).add(Items.STRING);
		tag(Tags.Items.TOOLS_SWORDS).add(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
		tag(Tags.Items.TOOLS_AXES).add(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
		tag(Tags.Items.TOOLS_PICKAXES).add(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
		tag(Tags.Items.TOOLS_SHOVELS).add(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
		tag(Tags.Items.TOOLS_HOES).add(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
		tag(Tags.Items.TOOLS_SHIELDS).add(Items.SHIELD);
		tag(Tags.Items.TOOLS_BOWS).add(Items.BOW);
		tag(Tags.Items.TOOLS_CROSSBOWS).add(Items.CROSSBOW);
		tag(Tags.Items.TOOLS_FISHING_RODS).add(Items.FISHING_ROD);
		tag(Tags.Items.TOOLS_TRIDENTS).add(Items.TRIDENT);
		tag(Tags.Items.TOOLS).addTag(Tags.Items.TOOLS_SWORDS).addTag(Tags.Items.TOOLS_AXES).addTag(Tags.Items.TOOLS_PICKAXES).addTag(Tags.Items.TOOLS_SHOVELS).addTag(Tags.Items.TOOLS_HOES).addTag(Tags.Items.TOOLS_SHIELDS).addTag(Tags.Items.TOOLS_BOWS).addTag(Tags.Items.TOOLS_CROSSBOWS).addTag(Tags.Items.TOOLS_FISHING_RODS).addTag(Tags.Items.TOOLS_TRIDENTS);
		tag(Tags.Items.ARMORS_HELMETS).add(Items.LEATHER_HELMET, Items.TURTLE_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
		tag(Tags.Items.ARMORS_CHESTPLATES).add(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
		tag(Tags.Items.ARMORS_LEGGINGS).add(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
		tag(Tags.Items.ARMORS_BOOTS).add(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
		tag(Tags.Items.ARMORS).addTag(Tags.Items.ARMORS_HELMETS).addTag(Tags.Items.ARMORS_CHESTPLATES).addTag(Tags.Items.ARMORS_LEGGINGS).addTag(Tags.Items.ARMORS_BOOTS);
	}

	private void addColored(Consumer<TagKey<Item>> consumer, TagKey<Item> group, String pattern) {
		String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
		for (DyeColor color : DyeColor.values()) {
			ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getName()));
			TagKey<Item> tag = getForgeItemTag(prefix + color.getName());
			Item item = BuiltInRegistries.ITEM.get(key);
			if (item == null || item == Items.AIR)
				throw new IllegalStateException("Unknown vanilla item: " + key.toString());
			tag(tag).add(item);
			consumer.accept(tag);
		}
	}

	private void copyColored(TagKey<Block> blockGroup, TagKey<Item> itemGroup) {
		String blockPre = blockGroup.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
		String itemPre = itemGroup.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
		for (DyeColor color : DyeColor.values()) {
			TagKey<Block> from = getForgeBlockTag(blockPre + color.getName());
			TagKey<Item> to = getForgeItemTag(itemPre + color.getName());
			copy(from, to);
		}
		copy(getForgeBlockTag(blockPre + "colorless"), getForgeItemTag(itemPre + "colorless"));
	}

	@SuppressWarnings("unchecked")
	private TagKey<Block> getForgeBlockTag(String name) {
		try {
			name = name.toUpperCase(Locale.ENGLISH).replace("_BLOCKS", "");;
			return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
		}
	}

	@SuppressWarnings("unchecked")
	private TagKey<Item> getForgeItemTag(String name) {
		try {
			name = name.toUpperCase(Locale.ENGLISH).replace("_BLOCKS", "");;
			return (TagKey<Item>) Tags.Items.class.getDeclaredField(name).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
		}
	}

	public FabricTagBuilder tag(TagKey<Item> tag) {
		return getOrCreateTagBuilder(tag);
	}
}
