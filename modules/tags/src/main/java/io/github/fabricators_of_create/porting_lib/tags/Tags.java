package io.github.fabricators_of_create.porting_lib.tags;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEnchantmentTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class Tags {
	public static class Blocks {
		// `neoforge` tags for functional behavior provided by NeoForge
		/**
		 * Controls what blocks Endermen cannot place blocks onto.
		 * <p>
		 * This is patched into the following method: {@link net.minecraft.world.entity.monster.EnderMan.EndermanLeaveBlockGoal#canPlaceBlock(Level, BlockPos, BlockState, BlockState, BlockState, BlockPos)}
		 */
		public static final TagKey<Block> ENDERMAN_PLACE_ON_BLACKLIST = neoforgeTag("enderman_place_on_blacklist");

		/**
		 * For denoting blocks that need tools that are Wood or higher to mine.
		 * By default, this is not added to any Minecraft tag since Wood is in the lowest "tier".
		 */
		public static final TagKey<Block> NEEDS_WOOD_TOOL = neoforgeTag("needs_wood_tool");

		/**
		 * For denoting blocks that need tools that are Gold or higher to mine.
		 * By default, this is not added to any Minecraft tag since Gold is in the lowest "tier".
		 */
		public static final TagKey<Block> NEEDS_GOLD_TOOL = neoforgeTag("needs_gold_tool");

		/**
		 * For denoting blocks that need tools that are Netherite or higher to mine.
		 * Blocks in this tag gets added to the following Minecraft tags:
		 * {@link BlockTags#INCORRECT_FOR_WOODEN_TOOL}
		 * {@link BlockTags#INCORRECT_FOR_STONE_TOOL}
		 * {@link BlockTags#INCORRECT_FOR_IRON_TOOL}
		 * {@link BlockTags#INCORRECT_FOR_GOLD_TOOL}
		 * {@link BlockTags#INCORRECT_FOR_DIAMOND_TOOL}
		 */
		public static final TagKey<Block> NEEDS_NETHERITE_TOOL = neoforgeTag("needs_netherite_tool");

		// `c` tags for common conventions
		public static final TagKey<Block> BARRELS = ConventionalBlockTags.BARRELS;
		public static final TagKey<Block> BARRELS_WOODEN = ConventionalBlockTags.WOODEN_BARRELS;
		public static final TagKey<Block> BOOKSHELVES = ConventionalBlockTags.BOOKSHELVES;
		/**
		 * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
		 */
		public static final TagKey<Block> BUDDING_BLOCKS = ConventionalBlockTags.BUDDING_BLOCKS;
		/**
		 * For blocks that are similar to amethyst where they have buddings forming from budding blocks
		 */
		public static final TagKey<Block> BUDS = ConventionalBlockTags.BUDS;
		public static final TagKey<Block> CHAINS = ConventionalBlockTags.CHAINS;
		public static final TagKey<Block> CHESTS = ConventionalBlockTags.CHESTS;
		public static final TagKey<Block> CHESTS_ENDER = ConventionalBlockTags.ENDER_CHESTS;
		public static final TagKey<Block> CHESTS_TRAPPED = ConventionalBlockTags.TRAPPED_CHESTS;
		public static final TagKey<Block> CHESTS_WOODEN = ConventionalBlockTags.WOODEN_CHESTS;
		/**
		 * For blocks that are similar to amethyst where they have clusters forming from budding blocks
		 */
		public static final TagKey<Block> CLUSTERS = ConventionalBlockTags.CLUSTERS;
		public static final TagKey<Block> COBBLESTONES = ConventionalBlockTags.COBBLESTONES;
		public static final TagKey<Block> COBBLESTONES_NORMAL = ConventionalBlockTags.NORMAL_COBBLESTONES;
		public static final TagKey<Block> COBBLESTONES_INFESTED = ConventionalBlockTags.INFESTED_COBBLESTONES;
		public static final TagKey<Block> COBBLESTONES_MOSSY = ConventionalBlockTags.MOSSY_COBBLESTONES;
		public static final TagKey<Block> COBBLESTONES_DEEPSLATE = ConventionalBlockTags.DEEPSLATE_COBBLESTONES;
		public static final TagKey<Block> CONCRETES = ConventionalBlockTags.CONCRETES;

		/**
		 * Tag that holds all blocks that can be dyed a specific color.
		 * (Does not include color blending blocks that would behave similar to leather armor item)
		 */
		public static final TagKey<Block> DYED = ConventionalBlockTags.DYED;
		public static final TagKey<Block> DYED_BLACK = ConventionalBlockTags.BLACK_DYED;
		public static final TagKey<Block> DYED_BLUE = ConventionalBlockTags.BLUE_DYED;
		public static final TagKey<Block> DYED_BROWN = ConventionalBlockTags.BROWN_DYED;
		public static final TagKey<Block> DYED_CYAN = ConventionalBlockTags.CYAN_DYED;
		public static final TagKey<Block> DYED_GRAY = ConventionalBlockTags.GRAY_DYED;
		public static final TagKey<Block> DYED_GREEN = ConventionalBlockTags.GREEN_DYED;
		public static final TagKey<Block> DYED_LIGHT_BLUE = ConventionalBlockTags.LIGHT_BLUE_DYED;
		public static final TagKey<Block> DYED_LIGHT_GRAY = ConventionalBlockTags.LIGHT_GRAY_DYED;
		public static final TagKey<Block> DYED_LIME = ConventionalBlockTags.LIME_DYED;
		public static final TagKey<Block> DYED_MAGENTA = ConventionalBlockTags.MAGENTA_DYED;
		public static final TagKey<Block> DYED_ORANGE = ConventionalBlockTags.ORANGE_DYED;
		public static final TagKey<Block> DYED_PINK = ConventionalBlockTags.PINK_DYED;
		public static final TagKey<Block> DYED_PURPLE = ConventionalBlockTags.PURPLE_DYED;
		public static final TagKey<Block> DYED_RED = ConventionalBlockTags.RED_DYED;
		public static final TagKey<Block> DYED_WHITE = ConventionalBlockTags.WHITE_DYED;
		public static final TagKey<Block> DYED_YELLOW = ConventionalBlockTags.YELLOW_DYED;
		public static final TagKey<Block> END_STONES = ConventionalBlockTags.END_STONES;
		public static final TagKey<Block> FENCE_GATES = ConventionalBlockTags.FENCE_GATES;
		public static final TagKey<Block> FENCE_GATES_WOODEN = ConventionalBlockTags.WOODEN_FENCE_GATES;
		public static final TagKey<Block> FENCES = ConventionalBlockTags.FENCES;
		public static final TagKey<Block> FENCES_NETHER_BRICK = ConventionalBlockTags.NETHER_BRICK_FENCES;
		public static final TagKey<Block> FENCES_WOODEN = ConventionalBlockTags.WOODEN_FENCES;

		public static final TagKey<Block> GLASS_BLOCKS = ConventionalBlockTags.GLASS_BLOCKS;
		public static final TagKey<Block> GLASS_BLOCKS_COLORLESS = ConventionalBlockTags.GLASS_BLOCKS_COLORLESS;
		/**
		 * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
		 */
		public static final TagKey<Block> GLASS_BLOCKS_CHEAP = ConventionalBlockTags.GLASS_BLOCKS_CHEAP;
		public static final TagKey<Block> GLASS_BLOCKS_TINTED = ConventionalBlockTags.GLASS_BLOCKS_TINTED;

		public static final TagKey<Block> GLASS_PANES = ConventionalBlockTags.GLASS_PANES;
		public static final TagKey<Block> GLASS_PANES_COLORLESS = ConventionalBlockTags.GLASS_PANES_COLORLESS;
		public static final TagKey<Block> GLAZED_TERRACOTTAS = ConventionalBlockTags.GLAZED_TERRACOTTAS;

		public static final TagKey<Block> GRAVELS = ConventionalBlockTags.GRAVELS;
		/**
		 * Tag that holds all blocks that recipe viewers should not show to users.
		 * Recipe viewers may use this to automatically find the corresponding BlockItem to hide.
		 */
		public static final TagKey<Block> HIDDEN_FROM_RECIPE_VIEWERS = ConventionalBlockTags.HIDDEN_FROM_RECIPE_VIEWERS;
		public static final TagKey<Block> NETHERRACKS = ConventionalBlockTags.NETHERRACKS;
		public static final TagKey<Block> OBSIDIANS = ConventionalBlockTags.OBSIDIANS;
		/**
		 * For common obsidian that has no special quirks or behaviors. Ideal for recipe use.
		 * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
		 */
		public static final TagKey<Block> OBSIDIANS_NORMAL = ConventionalBlockTags.NORMAL_OBSIDIANS;
		public static final TagKey<Block> OBSIDIANS_CRYING = ConventionalBlockTags.CRYING_OBSIDIANS;
		/**
		 * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORE_BEARING_GROUND_DEEPSLATE = ConventionalBlockTags.ORE_BEARING_GROUND_DEEPSLATE;
		/**
		 * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORE_BEARING_GROUND_NETHERRACK = ConventionalBlockTags.ORE_BEARING_GROUND_NETHERRACK;
		/**
		 * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORE_BEARING_GROUND_STONE = ConventionalBlockTags.ORE_BEARING_GROUND_STONE;
		/**
		 * Ores which on average result in more than one resource worth of materials ignoring fortune and other modifiers.
		 * (example, Copper Ore)
		 */
		public static final TagKey<Block> ORE_RATES_DENSE = ConventionalBlockTags.ORE_RATES_DENSE;
		/**
		 * Ores which on average result in one resource worth of materials ignoring fortune and other modifiers.
		 * (Example, Iron Ore)
		 */
		public static final TagKey<Block> ORE_RATES_SINGULAR = ConventionalBlockTags.ORE_RATES_SINGULAR;
		/**
		 * Ores which on average result in less than one resource worth of materials ignoring fortune and other modifiers.
		 * (Example, Nether Gold Ore as it drops 2 to 6 Gold Nuggets which is less than normal Gold Ore's Raw Gold drop)
		 */
		public static final TagKey<Block> ORE_RATES_SPARSE = ConventionalBlockTags.ORE_RATES_SPARSE;
		public static final TagKey<Block> ORES = ConventionalBlockTags.ORES;
		public static final TagKey<Block> ORES_COAL = ConventionalBlockTags.COAL_ORES;
		public static final TagKey<Block> ORES_COPPER = ConventionalBlockTags.COPPER_ORES;
		public static final TagKey<Block> ORES_DIAMOND = ConventionalBlockTags.DIAMOND_ORES;
		public static final TagKey<Block> ORES_EMERALD = ConventionalBlockTags.EMERALD_ORES;
		public static final TagKey<Block> ORES_GOLD = ConventionalBlockTags.GOLD_ORES;
		public static final TagKey<Block> ORES_IRON = ConventionalBlockTags.IRON_ORES;
		public static final TagKey<Block> ORES_LAPIS = ConventionalBlockTags.LAPIS_ORES;
		public static final TagKey<Block> ORES_NETHERITE_SCRAP = ConventionalBlockTags.NETHERITE_SCRAP_ORES;
		public static final TagKey<Block> ORES_QUARTZ = ConventionalBlockTags.QUARTZ_ORES;
		public static final TagKey<Block> ORES_REDSTONE = ConventionalBlockTags.REDSTONE_ORES;
		/**
		 * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = ConventionalBlockTags.ORES_IN_GROUND_DEEPSLATE;
		/**
		 * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORES_IN_GROUND_NETHERRACK = ConventionalBlockTags.ORES_IN_GROUND_NETHERRACK;
		/**
		 * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Block> ORES_IN_GROUND_STONE = ConventionalBlockTags.ORES_IN_GROUND_STONE;
		public static final TagKey<Block> PUMPKINS = ConventionalBlockTags.PUMPKINS;
		/**
		 * For pumpkins that are not carved.
		 */
		public static final TagKey<Block> PUMPKINS_NORMAL = ConventionalBlockTags.NORMAL_PUMPKINS;
		/**
		 * For pumpkins that are already carved but not a light source.
		 */
		public static final TagKey<Block> PUMPKINS_CARVED = ConventionalBlockTags.CARVED_PUMPKINS;

		/**
		 * For pumpkins that are already carved and a light source.
		 */
		public static final TagKey<Block> PUMPKINS_JACK_O_LANTERNS = ConventionalBlockTags.JACK_O_LANTERNS_PUMPKINS;
		public static final TagKey<Block> PLAYER_WORKSTATIONS_CRAFTING_TABLES = ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES;
		public static final TagKey<Block> PLAYER_WORKSTATIONS_FURNACES = ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES;
		/**
		 * Blocks should be included in this tag if their movement/relocation can cause serious issues such
		 * as world corruption upon being moved or for balance reason where the block should not be able to be relocated.
		 * Example: Chunk loaders or pipes where other mods that move blocks do not respect
		 * {@link BlockBehaviour.BlockStateBase#getPistonPushReaction}.
		 */
		public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = ConventionalBlockTags.RELOCATION_NOT_SUPPORTED;
		public static final TagKey<Block> ROPES = ConventionalBlockTags.ROPES;

		public static final TagKey<Block> SANDS = ConventionalBlockTags.SANDS;
		public static final TagKey<Block> SANDS_COLORLESS = ConventionalBlockTags.COLORLESS_SANDS;
		public static final TagKey<Block> SANDS_RED = ConventionalBlockTags.RED_SANDS;

		public static final TagKey<Block> SANDSTONE_BLOCKS = ConventionalBlockTags.SANDSTONE_BLOCKS;
		public static final TagKey<Block> SANDSTONE_SLABS = ConventionalBlockTags.SANDSTONE_SLABS;
		public static final TagKey<Block> SANDSTONE_STAIRS = ConventionalBlockTags.SANDSTONE_STAIRS;
		public static final TagKey<Block> SANDSTONE_RED_BLOCKS = ConventionalBlockTags.RED_SANDSTONE_BLOCKS;
		public static final TagKey<Block> SANDSTONE_RED_SLABS = ConventionalBlockTags.RED_SANDSTONE_SLABS;
		public static final TagKey<Block> SANDSTONE_RED_STAIRS = ConventionalBlockTags.RED_SANDSTONE_STAIRS;
		public static final TagKey<Block> SANDSTONE_UNCOLORED_BLOCKS = ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS;
		public static final TagKey<Block> SANDSTONE_UNCOLORED_SLABS = ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS;
		public static final TagKey<Block> SANDSTONE_UNCOLORED_STAIRS = ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS;
		/**
		 * Tag that holds all head based blocks such as Skeleton Skull or Player Head. (Named skulls to match minecraft:skulls item tag)
		 */
		public static final TagKey<Block> SKULLS = ConventionalBlockTags.SKULLS;
		/**
		 * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
		 */
		public static final TagKey<Block> STONES = ConventionalBlockTags.STONES;
		/**
		 * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
		 * and has a mirror recipe to reverse the crafting with no loss in resources.
		 * <p>
		 * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
		 * and so, it is considered a special case and not given a storage block tag.
		 */
		public static final TagKey<Block> STORAGE_BLOCKS = ConventionalBlockTags.STORAGE_BLOCKS;
		public static final TagKey<Block> STORAGE_BLOCKS_BONE_MEAL = ConventionalBlockTags.STORAGE_BLOCKS_BONE_MEAL;
		public static final TagKey<Block> STORAGE_BLOCKS_COAL = ConventionalBlockTags.STORAGE_BLOCKS_COAL;
		public static final TagKey<Block> STORAGE_BLOCKS_COPPER = ConventionalBlockTags.STORAGE_BLOCKS_COPPER;
		public static final TagKey<Block> STORAGE_BLOCKS_DIAMOND = ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND;
		public static final TagKey<Block> STORAGE_BLOCKS_DRIED_KELP = ConventionalBlockTags.STORAGE_BLOCKS_DRIED_KELP;
		public static final TagKey<Block> STORAGE_BLOCKS_EMERALD = ConventionalBlockTags.STORAGE_BLOCKS_EMERALD;
		public static final TagKey<Block> STORAGE_BLOCKS_GOLD = ConventionalBlockTags.STORAGE_BLOCKS_GOLD;
		public static final TagKey<Block> STORAGE_BLOCKS_IRON = ConventionalBlockTags.STORAGE_BLOCKS_IRON;
		public static final TagKey<Block> STORAGE_BLOCKS_LAPIS = ConventionalBlockTags.STORAGE_BLOCKS_LAPIS;
		public static final TagKey<Block> STORAGE_BLOCKS_NETHERITE = ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE;
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_COPPER = ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER;
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_GOLD = ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD;
		public static final TagKey<Block> STORAGE_BLOCKS_RAW_IRON = ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON;
		public static final TagKey<Block> STORAGE_BLOCKS_REDSTONE = ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE;
		public static final TagKey<Block> STORAGE_BLOCKS_SLIME = ConventionalBlockTags.STORAGE_BLOCKS_SLIME;
		public static final TagKey<Block> STORAGE_BLOCKS_WHEAT = ConventionalBlockTags.STORAGE_BLOCKS_WHEAT;
		public static final TagKey<Block> STRIPPED_LOGS = ConventionalBlockTags.STRIPPED_LOGS;
		public static final TagKey<Block> STRIPPED_WOODS = ConventionalBlockTags.STRIPPED_WOODS;
		public static final TagKey<Block> VILLAGER_JOB_SITES = ConventionalBlockTags.VILLAGER_JOB_SITES;

		/**
		 * Blocks tagged here will be tracked by Farmer Villagers who will attempt to plant crops on top.
		 */
		@Deprecated // Current not implemented
		public static final TagKey<Block> VILLAGER_FARMLANDS = neoforgeTag("villager_farmlands");

		private static TagKey<Block> tag(String name) {
			return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", name));
		}

		private static TagKey<Block> neoforgeTag(String name) {
			return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("neoforge", name));
		}
	}

	public static class EntityTypes {
		public static final TagKey<EntityType<?>> BOSSES = ConventionalEntityTypeTags.BOSSES;
		public static final TagKey<EntityType<?>> MINECARTS = ConventionalEntityTypeTags.MINECARTS;
		public static final TagKey<EntityType<?>> BOATS = ConventionalEntityTypeTags.BOATS;

		/**
		 * Entities should be included in this tag if they are not allowed to be picked up by items or grabbed in a way
		 * that a player can easily move the entity to anywhere they want. Ideal for special entities that should not
		 * be able to be put into a mob jar for example.
		 */
		public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED;

		/**
		 * Entities should be included in this tag if they are not allowed to be teleported in any way.
		 * This is more for mods that allow teleporting entities within the same dimension. Any mod that is
		 * teleporting entities to new dimensions should be checking canChangeDimensions method on the entity itself.
		 */
		public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED;

		private static TagKey<EntityType<?>> tag(String name) {
			return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", name));
		}
	}

	public static class Items {
		// `neoforge` tags for functional behavior provided by NeoForge
		/**
		 * Controls what items can be consumed for enchanting such as Enchanting Tables.
		 * This tag defaults to {@link net.minecraft.world.item.Items#LAPIS_LAZULI} when not present in any datapacks, including forge client on vanilla server
		 */
		public static final TagKey<Item> ENCHANTING_FUELS = neoforgeTag("enchanting_fuels");

		// `c` tags for common conventions
		public static final TagKey<Item> BARRELS = ConventionalItemTags.BARRELS;
		public static final TagKey<Item> BARRELS_WOODEN = ConventionalItemTags.WOODEN_BARRELS;
		public static final TagKey<Item> BONES = ConventionalItemTags.BONES;
		public static final TagKey<Item> BOOKSHELVES = ConventionalItemTags.BOOKSHELVES;
		public static final TagKey<Item> BRICKS = ConventionalItemTags.BRICKS;
		public static final TagKey<Item> BRICKS_NORMAL = ConventionalItemTags.NORMAL_BRICKS;
		public static final TagKey<Item> BRICKS_NETHER = ConventionalItemTags.NETHER_BRICKS;
		public static final TagKey<Item> BUCKETS = ConventionalItemTags.BUCKETS;
		public static final TagKey<Item> BUCKETS_EMPTY = ConventionalItemTags.EMPTY_BUCKETS;
		/**
		 * Does not include entity water buckets.
		 * If checking for the fluid this bucket holds in code, please use {@link net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper#getFluid} instead.
		 */
		public static final TagKey<Item> BUCKETS_WATER = ConventionalItemTags.WATER_BUCKETS;
		/**
		 * If checking for the fluid this bucket holds in code, please use {@link net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper#getFluid} instead.
		 */
		public static final TagKey<Item> BUCKETS_LAVA = ConventionalItemTags.LAVA_BUCKETS;
		public static final TagKey<Item> BUCKETS_MILK = ConventionalItemTags.MILK_BUCKETS;
		public static final TagKey<Item> BUCKETS_POWDER_SNOW = ConventionalItemTags.POWDER_SNOW_BUCKETS;
		public static final TagKey<Item> BUCKETS_ENTITY_WATER = ConventionalItemTags.ENTITY_WATER_BUCKETS;
		/**
		 * For blocks that are similar to amethyst where their budding block produces buds and cluster blocks
		 */
		public static final TagKey<Item> BUDDING_BLOCKS = ConventionalItemTags.BUDDING_BLOCKS;
		/**
		 * For blocks that are similar to amethyst where they have buddings forming from budding blocks
		 */
		public static final TagKey<Item> BUDS = ConventionalItemTags.BUDS;
		public static final TagKey<Item> CHAINS = ConventionalItemTags.CHAINS;
		public static final TagKey<Item> CHESTS = ConventionalItemTags.CHESTS;
		public static final TagKey<Item> CHESTS_ENDER = ConventionalItemTags.ENDER_CHESTS;
		public static final TagKey<Item> CHESTS_TRAPPED = ConventionalItemTags.TRAPPED_CHESTS;
		public static final TagKey<Item> CHESTS_WOODEN = ConventionalItemTags.WOODEN_CHESTS;
		public static final TagKey<Item> COBBLESTONES = ConventionalItemTags.COBBLESTONES;
		public static final TagKey<Item> COBBLESTONES_NORMAL = ConventionalItemTags.NORMAL_COBBLESTONES;
		public static final TagKey<Item> COBBLESTONES_INFESTED = ConventionalItemTags.INFESTED_COBBLESTONES;
		public static final TagKey<Item> COBBLESTONES_MOSSY = ConventionalItemTags.MOSSY_COBBLESTONES;
		public static final TagKey<Item> COBBLESTONES_DEEPSLATE = ConventionalItemTags.DEEPSLATE_COBBLESTONES;
		public static final TagKey<Item> CONCRETES = ConventionalItemTags.CONCRETES;
		/**
		 * Block tag equivalent is {@link BlockTags#CONCRETE_POWDER}
		 */
		public static final TagKey<Item> CONCRETE_POWDERS = ConventionalItemTags.CONCRETE_POWDERS;
		/**
		 * For blocks that are similar to amethyst where they have clusters forming from budding blocks
		 */
		public static final TagKey<Item> CLUSTERS = ConventionalItemTags.CLUSTERS;
		/**
		 * For raw materials harvested from growable plants. Crop items can be edible like carrots or
		 * non-edible like wheat and cocoa beans.
		 */
		public static final TagKey<Item> CROPS = ConventionalItemTags.CROPS;
		public static final TagKey<Item> CROPS_BEETROOT = ConventionalItemTags.BEETROOT_CROPS;
		public static final TagKey<Item> CROPS_CACTUS = ConventionalItemTags.CACTUS_CROPS;
		public static final TagKey<Item> CROPS_CARROT = ConventionalItemTags.CARROT_CROPS;
		public static final TagKey<Item> CROPS_COCOA_BEAN = ConventionalItemTags.COCOA_BEAN_CROPS;
		public static final TagKey<Item> CROPS_MELON = ConventionalItemTags.MELON_CROPS;
		public static final TagKey<Item> CROPS_NETHER_WART = ConventionalItemTags.NETHER_WART_CROPS;
		public static final TagKey<Item> CROPS_POTATO = ConventionalItemTags.POTATO_CROPS;
		public static final TagKey<Item> CROPS_PUMPKIN = ConventionalItemTags.PUMPKIN_CROPS;
		public static final TagKey<Item> CROPS_SUGAR_CANE = ConventionalItemTags.SUGAR_CANE_CROPS;
		public static final TagKey<Item> CROPS_WHEAT = ConventionalItemTags.WHEAT_CROPS;

		/**
		 * Drinks are defined as (1) consumable items that (2) use the
		 * {@linkplain net.minecraft.world.item.ItemUseAnimation#DRINK drink item use animation}, (3) can be consumed regardless of the
		 * player's current hunger.
		 *
		 * <p>Drinks may provide nutrition and saturation, but are not required to do so.
		 *
		 * <p>More specific types of drinks, such as Water, Milk, or Juice should be placed in a sub-tag, such as
		 * {@code #c:drinks/water}, {@code #c:drinks/milk}, and {@code #c:drinks/juice}.
		 */
		public static final TagKey<Item> DRINKS = ConventionalItemTags.DRINKS;
		/**
		 * For consumable drinks that contain only water.
		 */
		public static final TagKey<Item> DRINKS_WATER = ConventionalItemTags.WATER_DRINKS;
		/**
		 * For consumable drinks that are generally watery (such as potions).
		 */
		public static final TagKey<Item> DRINKS_WATERY = ConventionalItemTags.WATERY_DRINKS;
		public static final TagKey<Item> DRINKS_MILK = ConventionalItemTags.MILK_DRINKS;
		public static final TagKey<Item> DRINKS_HONEY = ConventionalItemTags.HONEY_DRINKS;
		/**
		 * For consumable drinks that are magic in nature and usually grant at least one
		 * {@link net.minecraft.world.effect.MobEffect} when consumed.
		 */
		public static final TagKey<Item> DRINKS_MAGIC = ConventionalItemTags.MAGIC_DRINKS;
		/**
		 * For drinks that always grant the {@linkplain net.minecraft.world.effect.MobEffects#BAD_OMEN Bad Omen} effect.
		 */
		public static final TagKey<Item> DRINKS_OMINOUS = ConventionalItemTags.OMINOUS_DRINKS;
		/**
		 * Plant based fruit and vegetable juices belong in this tag, for example apple juice and carrot juice.
		 *
		 * <p>If tags for specific types of juices are desired, they may go in a sub-tag, using their regular name such as
		 * {@code #c:drinks/apple_juice}.
		 */
		public static final TagKey<Item> DRINKS_JUICE = ConventionalItemTags.JUICE_DRINKS;

		/**
		 * For non-empty bottles that are {@linkplain #DRINKS drinkable}.
		 */
		public static final TagKey<Item> DRINK_CONTAINING_BOTTLE = ConventionalItemTags.DRINK_CONTAINING_BOTTLE;
		/**
		 * For non-empty buckets that are {@linkplain #DRINKS drinkable}.
		 */
		public static final TagKey<Item> DRINK_CONTAINING_BUCKET = ConventionalItemTags.DRINK_CONTAINING_BUCKET;

		public static final TagKey<Item> DUSTS = ConventionalItemTags.DUSTS;
		public static final TagKey<Item> DUSTS_REDSTONE = ConventionalItemTags.REDSTONE_DUSTS;
		public static final TagKey<Item> DUSTS_GLOWSTONE = ConventionalItemTags.GLOWSTONE_DUSTS;

		/**
		 * Tag that holds all blocks and items that can be dyed a specific color.
		 * (Does not include color blending items like leather armor
		 * Use {@link net.minecraft.tags.ItemTags#DYEABLE} tag instead for color blending items)
		 * <p>
		 * Note: Use custom ingredients in recipes to do tag intersections and/or tag exclusions
		 * to make more powerful recipes utilizing multiple tags such as dyed tags for an ingredient.
		 * See {@link net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients}
		 * for various custom ingredients available that can also be used in data generation.
		 */
		public static final TagKey<Item> DYED = ConventionalItemTags.DYED;
		public static final TagKey<Item> DYED_BLACK = ConventionalItemTags.BLACK_DYED;
		public static final TagKey<Item> DYED_BLUE = ConventionalItemTags.BLUE_DYED;
		public static final TagKey<Item> DYED_BROWN = ConventionalItemTags.BROWN_DYED;
		public static final TagKey<Item> DYED_CYAN = ConventionalItemTags.CYAN_DYED;
		public static final TagKey<Item> DYED_GRAY = ConventionalItemTags.GRAY_DYED;
		public static final TagKey<Item> DYED_GREEN = ConventionalItemTags.GREEN_DYED;
		public static final TagKey<Item> DYED_LIGHT_BLUE = ConventionalItemTags.LIGHT_BLUE_DYED;
		public static final TagKey<Item> DYED_LIGHT_GRAY = ConventionalItemTags.LIGHT_GRAY_DYED;
		public static final TagKey<Item> DYED_LIME = ConventionalItemTags.LIME_DYED;
		public static final TagKey<Item> DYED_MAGENTA = ConventionalItemTags.MAGENTA_DYED;
		public static final TagKey<Item> DYED_ORANGE = ConventionalItemTags.ORANGE_DYED;
		public static final TagKey<Item> DYED_PINK = ConventionalItemTags.PINK_DYED;
		public static final TagKey<Item> DYED_PURPLE = ConventionalItemTags.PURPLE_DYED;
		public static final TagKey<Item> DYED_RED = ConventionalItemTags.RED_DYED;
		public static final TagKey<Item> DYED_WHITE = ConventionalItemTags.WHITE_DYED;
		public static final TagKey<Item> DYED_YELLOW = ConventionalItemTags.YELLOW_DYED;

		public static final TagKey<Item> DYES = ConventionalItemTags.DYES;
		public static final TagKey<Item> DYES_BLACK = DyeColor.BLACK.port_lib$getTag();
		public static final TagKey<Item> DYES_RED = DyeColor.RED.port_lib$getTag();
		public static final TagKey<Item> DYES_GREEN = DyeColor.GREEN.port_lib$getTag();
		public static final TagKey<Item> DYES_BROWN = DyeColor.BROWN.port_lib$getTag();
		public static final TagKey<Item> DYES_BLUE = DyeColor.BLUE.port_lib$getTag();
		public static final TagKey<Item> DYES_PURPLE = DyeColor.PURPLE.port_lib$getTag();
		public static final TagKey<Item> DYES_CYAN = DyeColor.CYAN.port_lib$getTag();
		public static final TagKey<Item> DYES_LIGHT_GRAY = DyeColor.LIGHT_GRAY.port_lib$getTag();
		public static final TagKey<Item> DYES_GRAY = DyeColor.GRAY.port_lib$getTag();
		public static final TagKey<Item> DYES_PINK = DyeColor.PINK.port_lib$getTag();
		public static final TagKey<Item> DYES_LIME = DyeColor.LIME.port_lib$getTag();
		public static final TagKey<Item> DYES_YELLOW = DyeColor.YELLOW.port_lib$getTag();
		public static final TagKey<Item> DYES_LIGHT_BLUE = DyeColor.LIGHT_BLUE.port_lib$getTag();
		public static final TagKey<Item> DYES_MAGENTA = DyeColor.MAGENTA.port_lib$getTag();
		public static final TagKey<Item> DYES_ORANGE = DyeColor.ORANGE.port_lib$getTag();
		public static final TagKey<Item> DYES_WHITE = DyeColor.WHITE.port_lib$getTag();

		/**
		 * For eggs to use for culinary purposes in recipes such as baking a cake.
		 */
		public static final TagKey<Item> EGGS = ConventionalItemTags.EGGS;
		public static final TagKey<Item> END_STONES = ConventionalItemTags.END_STONES;
		public static final TagKey<Item> ENDER_PEARLS = ConventionalItemTags.ENDER_PEARLS;
		public static final TagKey<Item> FEATHERS = ConventionalItemTags.FEATHERS;
		public static final TagKey<Item> FENCE_GATES = ConventionalItemTags.FENCE_GATES;
		public static final TagKey<Item> FENCE_GATES_WOODEN = ConventionalItemTags.WOODEN_FENCE_GATES;
		public static final TagKey<Item> FENCES = ConventionalItemTags.FENCES;
		public static final TagKey<Item> FENCES_NETHER_BRICK = ConventionalItemTags.NETHER_BRICK_FENCES;
		public static final TagKey<Item> FENCES_WOODEN = ConventionalItemTags.WOODEN_FENCES;
		/**
		 * For bonemeal-like items that can grow plants.
		 * (Note: Could include durability-based modded bonemeal-like items. Check for durability {@link net.minecraft.core.component.DataComponents#DAMAGE} DataComponent to handle them properly)
		 */
		public static final TagKey<Item> FERTILIZERS = ConventionalItemTags.FERTILIZERS;
		public static final TagKey<Item> FOODS = ConventionalItemTags.FOODS;
		/**
		 * Apples and other foods that are considered fruits in the culinary field belong in this tag.
		 * Cherries would go here as they are considered a "stone fruit" within culinary fields.
		 */
		public static final TagKey<Item> FOODS_FRUIT = ConventionalItemTags.FRUIT_FOODS;
		/**
		 * Tomatoes and other foods that are considered vegetables in the culinary field belong in this tag.
		 */
		public static final TagKey<Item> FOODS_VEGETABLE = ConventionalItemTags.VEGETABLE_FOODS;
		/**
		 * Strawberries, raspberries, and other berry foods belong in this tag.
		 * Cherries would NOT go here as they are considered a "stone fruit" within culinary fields.
		 */
		public static final TagKey<Item> FOODS_BERRY = ConventionalItemTags.BERRY_FOODS;
		public static final TagKey<Item> FOODS_BREAD = ConventionalItemTags.BREAD_FOODS;
		public static final TagKey<Item> FOODS_COOKIE = ConventionalItemTags.COOKIE_FOODS;
		public static final TagKey<Item> FOODS_RAW_MEAT = ConventionalItemTags.RAW_MEAT_FOODS;
		public static final TagKey<Item> FOODS_COOKED_MEAT = ConventionalItemTags.COOKED_MEAT_FOODS;
		public static final TagKey<Item> FOODS_RAW_FISH = ConventionalItemTags.RAW_FISH_FOODS;
		public static final TagKey<Item> FOODS_COOKED_FISH = ConventionalItemTags.COOKED_FISH_FOODS;
		/**
		 * Soups, stews, and other liquid food in bowls belongs in this tag.
		 */
		public static final TagKey<Item> FOODS_SOUP = ConventionalItemTags.SOUP_FOODS;
		/**
		 * Sweets and candies like lollipops or chocolate belong in this tag.
		 */
		public static final TagKey<Item> FOODS_CANDY = ConventionalItemTags.CANDY_FOODS;
		/**
		 * Pies and other pie-like foods belong in this tag.
		 */
		public static final TagKey<Item> FOODS_PIE = ConventionalItemTags.PIE_FOODS;
		/**
		 * Any gold-based foods would go in this tag. Such as Golden Apples or Glistering Melon Slice.
		 */
		public static final TagKey<Item> FOODS_GOLDEN = ConventionalItemTags.GOLDEN_FOODS;
		/**
		 * Foods like cake that can be eaten when placed in the world belong in this tag.
		 */
		public static final TagKey<Item> FOODS_EDIBLE_WHEN_PLACED = ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS;
		/**
		 * For foods that inflict food poisoning-like effects.
		 * Examples are Rotten Flesh's Hunger or Pufferfish's Nausea, or Poisonous Potato's Poison.
		 */
		public static final TagKey<Item> FOODS_FOOD_POISONING = ConventionalItemTags.FOOD_POISONING_FOODS;
		/**
		 * All foods edible by animals excluding poisonous foods.
		 * (Does not include {@link ItemTags#PARROT_POISONOUS_FOOD})
		 */
		public static final TagKey<Item> ANIMAL_FOODS = ConventionalItemTags.ANIMAL_FOODS;
		public static final TagKey<Item> GEMS = ConventionalItemTags.GEMS;
		public static final TagKey<Item> GEMS_DIAMOND = ConventionalItemTags.DIAMOND_GEMS;
		public static final TagKey<Item> GEMS_EMERALD = ConventionalItemTags.EMERALD_GEMS;
		public static final TagKey<Item> GEMS_AMETHYST = ConventionalItemTags.AMETHYST_GEMS;
		public static final TagKey<Item> GEMS_LAPIS = ConventionalItemTags.LAPIS_GEMS;
		public static final TagKey<Item> GEMS_PRISMARINE = ConventionalItemTags.PRISMARINE_GEMS;
		public static final TagKey<Item> GEMS_QUARTZ = ConventionalItemTags.QUARTZ_GEMS;

		public static final TagKey<Item> GLASS_BLOCKS = ConventionalItemTags.GLASS_BLOCKS;
		public static final TagKey<Item> GLASS_BLOCKS_COLORLESS = ConventionalItemTags.GLASS_BLOCKS_COLORLESS;
		/**
		 * Glass which is made from cheap resources like sand and only minor additional ingredients like dyes
		 */
		public static final TagKey<Item> GLASS_BLOCKS_CHEAP = ConventionalItemTags.GLASS_BLOCKS_CHEAP;
		public static final TagKey<Item> GLASS_BLOCKS_TINTED = ConventionalItemTags.GLASS_BLOCKS_TINTED;

		public static final TagKey<Item> GLASS_PANES = ConventionalItemTags.GLASS_PANES;
		public static final TagKey<Item> GLASS_PANES_COLORLESS = ConventionalItemTags.GLASS_PANES_COLORLESS;
		public static final TagKey<Item> GLAZED_TERRACOTTAS = ConventionalItemTags.GLAZED_TERRACOTTAS;

		public static final TagKey<Item> GRAVELS = ConventionalItemTags.GRAVELS;
		public static final TagKey<Item> GUNPOWDERS = ConventionalItemTags.GUNPOWDERS;
		/**
		 * Tag that holds all items that recipe viewers should not show to users.
		 */
		public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = ConventionalItemTags.HIDDEN_FROM_RECIPE_VIEWERS;
		public static final TagKey<Item> INGOTS = ConventionalItemTags.INGOTS;
		public static final TagKey<Item> INGOTS_COPPER = ConventionalItemTags.COPPER_INGOTS;
		public static final TagKey<Item> INGOTS_GOLD = ConventionalItemTags.GOLD_INGOTS;
		public static final TagKey<Item> INGOTS_IRON = ConventionalItemTags.IRON_INGOTS;
		public static final TagKey<Item> INGOTS_NETHERITE = ConventionalItemTags.NETHERITE_INGOTS;
		public static final TagKey<Item> LEATHERS = ConventionalItemTags.LEATHERS;
		/**
		 * Small mushroom items. Not the full block forms.
		 */
		public static final TagKey<Item> MUSHROOMS = ConventionalItemTags.MUSHROOMS;
		/**
		 * For music disc-like materials to be used in recipes.
		 * A pancake with a JUKEBOX_PLAYABLE component attached to play in Jukeboxes as an Easter Egg is not a music disc and would not go in this tag.
		 */
		public static final TagKey<Item> MUSIC_DISCS = ConventionalItemTags.MUSIC_DISCS;
		public static final TagKey<Item> NETHER_STARS = ConventionalItemTags.NETHER_STARS;
		public static final TagKey<Item> NETHERRACKS = ConventionalItemTags.NETHERRACKS;
		public static final TagKey<Item> NUGGETS = ConventionalItemTags.NUGGETS;
		public static final TagKey<Item> NUGGETS_GOLD = ConventionalItemTags.GOLD_NUGGETS;
		public static final TagKey<Item> NUGGETS_IRON = ConventionalItemTags.IRON_NUGGETS;
		public static final TagKey<Item> OBSIDIANS = ConventionalItemTags.OBSIDIANS;
		/**
		 * For common obsidian that has no special quirks or behaviors. Ideal for recipe use.
		 * Crying Obsidian, for example, is a light block and harder to obtain. So it gets its own tag instead of being under normal tag.
		 */
		public static final TagKey<Item> OBSIDIANS_NORMAL = ConventionalItemTags.NORMAL_OBSIDIANS;
		public static final TagKey<Item> OBSIDIANS_CRYING = ConventionalItemTags.CRYING_OBSIDIANS;
		/**
		 * Blocks which are often replaced by deepslate ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_DEEPSLATE}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORE_BEARING_GROUND_DEEPSLATE = ConventionalItemTags.ORE_BEARING_GROUND_DEEPSLATE;
		/**
		 * Blocks which are often replaced by netherrack ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_NETHERRACK}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORE_BEARING_GROUND_NETHERRACK = ConventionalItemTags.ORE_BEARING_GROUND_NETHERRACK;
		/**
		 * Blocks which are often replaced by stone ores, i.e. the ores in the tag {@link #ORES_IN_GROUND_STONE}, during world generation.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORE_BEARING_GROUND_STONE = ConventionalItemTags.ORE_BEARING_GROUND_STONE;
		/**
		 * Ores which on average result in more than one resource worth of materials ignoring fortune and other modifiers.
		 * (example, Copper Ore)
		 */
		public static final TagKey<Item> ORE_RATES_DENSE = ConventionalItemTags.ORE_RATES_DENSE;
		/**
		 * Ores which on average result in one resource worth of materials ignoring fortune and other modifiers.
		 * (Example, Iron Ore)
		 */
		public static final TagKey<Item> ORE_RATES_SINGULAR = ConventionalItemTags.ORE_RATES_SINGULAR;
		/**
		 * Ores which on average result in less than one resource worth of materials ignoring fortune and other modifiers.
		 * (Example, Nether Gold Ore as it drops 2 to 6 Gold Nuggets which is less than normal Gold Ore's Raw Gold drop)
		 */
		public static final TagKey<Item> ORE_RATES_SPARSE = ConventionalItemTags.ORE_RATES_SPARSE;
		public static final TagKey<Item> ORES = ConventionalItemTags.ORES;
		public static final TagKey<Item> ORES_COAL = ConventionalItemTags.COAL_ORES;
		public static final TagKey<Item> ORES_COPPER = ConventionalItemTags.COPPER_ORES;
		public static final TagKey<Item> ORES_DIAMOND = ConventionalItemTags.DIAMOND_ORES;
		public static final TagKey<Item> ORES_EMERALD = ConventionalItemTags.EMERALD_ORES;
		public static final TagKey<Item> ORES_GOLD = ConventionalItemTags.GOLD_ORES;
		public static final TagKey<Item> ORES_IRON = ConventionalItemTags.IRON_ORES;
		public static final TagKey<Item> ORES_LAPIS = ConventionalItemTags.LAPIS_ORES;
		public static final TagKey<Item> ORES_NETHERITE_SCRAP = ConventionalItemTags.NETHERITE_SCRAP_ORES;
		public static final TagKey<Item> ORES_QUARTZ = ConventionalItemTags.QUARTZ_ORES;
		public static final TagKey<Item> ORES_REDSTONE = ConventionalItemTags.REDSTONE_ORES;
		/**
		 * Ores in deepslate (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_DEEPSLATE}) which could logically use deepslate as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = ConventionalItemTags.ORES_IN_GROUND_DEEPSLATE;
		/**
		 * Ores in netherrack (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_NETHERRACK}) which could logically use netherrack as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORES_IN_GROUND_NETHERRACK = ConventionalItemTags.ORES_IN_GROUND_NETHERRACK;
		/**
		 * Ores in stone (or in equivalent blocks in the tag {@link #ORE_BEARING_GROUND_STONE}) which could logically use stone as recipe input or output.
		 * (The block's registry name is used as the tag name)
		 */
		public static final TagKey<Item> ORES_IN_GROUND_STONE = ConventionalItemTags.ORES_IN_GROUND_STONE;
		public static final TagKey<Item> PLAYER_WORKSTATIONS_CRAFTING_TABLES = ConventionalItemTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES;
		public static final TagKey<Item> PLAYER_WORKSTATIONS_FURNACES = ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES;
		/**
		 * Items that can hold various potion effects by making use of {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}.
		 * Contents of this tag may not always be a kind of bottle. Buckets of potions could go here.
		 * The subtags would be the name of the container that is holding the potion effects such as `c:potions/bucket` or `c:potions/vial` as examples.
		 */
		public static final TagKey<Item> POTIONS = ConventionalItemTags.POTIONS;
		/**
		 * Variations of the potion bottle that can hold various effects by using {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}.
		 * Examples are splash and lingering potions from vanilla.
		 * If a mod adds a new variant like a seeking potion that applies effect to the closest entity at impact, that would in this tag.
		 */
		public static final TagKey<Item> POTION_BOTTLE = ConventionalItemTags.BOTTLE_POTIONS;
		public static final TagKey<Item> PUMPKINS = ConventionalItemTags.PUMPKINS;
		/**
		 * For pumpkins that are not carved.
		 */
		public static final TagKey<Item> PUMPKINS_NORMAL = ConventionalItemTags.NORMAL_PUMPKINS;
		/**
		 * For pumpkins that are already carved but not a light source.
		 */
		public static final TagKey<Item> PUMPKINS_CARVED = ConventionalItemTags.CARVED_PUMPKINS;

		/**
		 * For pumpkins that are already carved and a light source.
		 */
		public static final TagKey<Item> PUMPKINS_JACK_O_LANTERNS = ConventionalItemTags.JACK_O_LANTERNS_PUMPKINS;
		public static final TagKey<Item> RAW_MATERIALS = ConventionalItemTags.RAW_MATERIALS;
		public static final TagKey<Item> RAW_MATERIALS_COPPER = ConventionalItemTags.COPPER_RAW_MATERIALS;
		public static final TagKey<Item> RAW_MATERIALS_GOLD = ConventionalItemTags.GOLD_RAW_MATERIALS;
		public static final TagKey<Item> RAW_MATERIALS_IRON = ConventionalItemTags.IRON_RAW_MATERIALS;
		/**
		 * For rod-like materials to be used in recipes.
		 */
		public static final TagKey<Item> RODS = ConventionalItemTags.RODS;
		public static final TagKey<Item> RODS_BLAZE = ConventionalItemTags.BLAZE_RODS;
		public static final TagKey<Item> RODS_BREEZE = ConventionalItemTags.BREEZE_RODS;
		/**
		 * For stick-like materials to be used in recipes.
		 * One example is a mod adds stick variants such as Spruce Sticks but would like stick recipes to be able to use it.
		 */
		public static final TagKey<Item> RODS_WOODEN = ConventionalItemTags.WOODEN_RODS;
		public static final TagKey<Item> ROPES = ConventionalItemTags.ROPES;

		public static final TagKey<Item> SANDS = ConventionalItemTags.SANDS;
		public static final TagKey<Item> SANDS_COLORLESS = ConventionalItemTags.COLORLESS_SANDS;
		public static final TagKey<Item> SANDS_RED = ConventionalItemTags.RED_SANDS;

		public static final TagKey<Item> SANDSTONE_BLOCKS = ConventionalItemTags.SANDSTONE_BLOCKS;
		public static final TagKey<Item> SANDSTONE_SLABS = ConventionalItemTags.SANDSTONE_SLABS;
		public static final TagKey<Item> SANDSTONE_STAIRS = ConventionalItemTags.SANDSTONE_STAIRS;
		public static final TagKey<Item> SANDSTONE_RED_BLOCKS = ConventionalItemTags.RED_SANDSTONE_BLOCKS;
		public static final TagKey<Item> SANDSTONE_RED_SLABS = ConventionalItemTags.RED_SANDSTONE_SLABS;
		public static final TagKey<Item> SANDSTONE_RED_STAIRS = ConventionalItemTags.RED_SANDSTONE_STAIRS;
		public static final TagKey<Item> SANDSTONE_UNCOLORED_BLOCKS = ConventionalItemTags.UNCOLORED_SANDSTONE_BLOCKS;
		public static final TagKey<Item> SANDSTONE_UNCOLORED_SLABS = ConventionalItemTags.UNCOLORED_SANDSTONE_SLABS;
		public static final TagKey<Item> SANDSTONE_UNCOLORED_STAIRS = ConventionalItemTags.UNCOLORED_SANDSTONE_STAIRS;

		/**
		 * For items that are explicitly seeds for use cases such as refilling a bird feeder block or certain seed-based recipes.
		 */
		public static final TagKey<Item> SEEDS = ConventionalItemTags.SEEDS;
		public static final TagKey<Item> SEEDS_BEETROOT = ConventionalItemTags.BEETROOT_SEEDS;
		public static final TagKey<Item> SEEDS_MELON = ConventionalItemTags.MELON_SEEDS;
		public static final TagKey<Item> SEEDS_PUMPKIN = ConventionalItemTags.PUMPKIN_SEEDS;
		public static final TagKey<Item> SEEDS_TORCHFLOWER = ConventionalItemTags.TORCHFLOWER_SEEDS;
		public static final TagKey<Item> SEEDS_WHEAT = ConventionalItemTags.WHEAT_SEEDS;
		/**
		 * Block tag equivalent is {@link BlockTags#SHULKER_BOXES}
		 */
		public static final TagKey<Item> SHULKER_BOXES = ConventionalItemTags.SHULKER_BOXES;
		public static final TagKey<Item> SLIME_BALLS = ConventionalItemTags.SLIME_BALLS;
		/**
		 * Natural stone-like blocks that can be used as a base ingredient in recipes that takes stone.
		 */
		public static final TagKey<Item> STONES = ConventionalItemTags.STONES;
		/**
		 * A storage block is generally a block that has a recipe to craft a bulk of 1 kind of resource to a block
		 * and has a mirror recipe to reverse the crafting with no loss in resources.
		 * <p>
		 * Honey Block is special in that the reversing recipe is not a perfect mirror of the crafting recipe
		 * and so, it is considered a special case and not given a storage block tag.
		 */
		public static final TagKey<Item> STORAGE_BLOCKS = ConventionalItemTags.STORAGE_BLOCKS;
		public static final TagKey<Item> STORAGE_BLOCKS_BONE_MEAL = ConventionalItemTags.STORAGE_BLOCKS_BONE_MEAL;
		public static final TagKey<Item> STORAGE_BLOCKS_COAL = ConventionalItemTags.STORAGE_BLOCKS_COAL;
		public static final TagKey<Item> STORAGE_BLOCKS_COPPER = ConventionalItemTags.STORAGE_BLOCKS_COPPER;
		public static final TagKey<Item> STORAGE_BLOCKS_DIAMOND = ConventionalItemTags.STORAGE_BLOCKS_DIAMOND;
		public static final TagKey<Item> STORAGE_BLOCKS_DRIED_KELP = ConventionalItemTags.STORAGE_BLOCKS_DRIED_KELP;
		public static final TagKey<Item> STORAGE_BLOCKS_EMERALD = ConventionalItemTags.STORAGE_BLOCKS_EMERALD;
		public static final TagKey<Item> STORAGE_BLOCKS_GOLD = ConventionalItemTags.STORAGE_BLOCKS_GOLD;
		public static final TagKey<Item> STORAGE_BLOCKS_IRON = ConventionalItemTags.STORAGE_BLOCKS_IRON;
		public static final TagKey<Item> STORAGE_BLOCKS_LAPIS = ConventionalItemTags.STORAGE_BLOCKS_LAPIS;
		public static final TagKey<Item> STORAGE_BLOCKS_NETHERITE = ConventionalItemTags.STORAGE_BLOCKS_NETHERITE;
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_COPPER = ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER;
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_GOLD = ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD;
		public static final TagKey<Item> STORAGE_BLOCKS_RAW_IRON = ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON;
		public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = ConventionalItemTags.STORAGE_BLOCKS_REDSTONE;
		public static final TagKey<Item> STORAGE_BLOCKS_SLIME = ConventionalItemTags.STORAGE_BLOCKS_SLIME;
		public static final TagKey<Item> STORAGE_BLOCKS_WHEAT = ConventionalItemTags.STORAGE_BLOCKS_WHEAT;
		public static final TagKey<Item> STRINGS = ConventionalItemTags.STRINGS;
		public static final TagKey<Item> STRIPPED_LOGS = ConventionalItemTags.STRIPPED_LOGS;
		public static final TagKey<Item> STRIPPED_WOODS = ConventionalItemTags.STRIPPED_WOODS;
		public static final TagKey<Item> VILLAGER_JOB_SITES = ConventionalItemTags.VILLAGER_JOB_SITES;

		// Tools and Armors
		/**
		 * A tag containing all existing tools. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS = ConventionalItemTags.TOOLS;
		/**
		 * A tag containing all existing shields. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_SHIELD = ConventionalItemTags.SHIELD_TOOLS;
		/**
		 * A tag containing all existing bows. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_BOW = ConventionalItemTags.BOW_TOOLS;
		/**
		 * A tag containing all existing crossbows. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_CROSSBOW = ConventionalItemTags.CROSSBOW_TOOLS;
		/**
		 * A tag containing all existing fishing rods. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_FISHING_ROD = ConventionalItemTags.FISHING_ROD_TOOLS;
		/**
		 * A tag containing all existing spears. Other tools such as throwing knives or boomerangs
		 * should not be put into this tag and should be put into their own tool tags.
		 * Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_SPEAR = ConventionalItemTags.SPEAR_TOOLS;
		/**
		 * A tag containing all existing shears. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_SHEAR = ConventionalItemTags.SHEAR_TOOLS;
		/**
		 * A tag containing all existing brushes. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_BRUSH = ConventionalItemTags.BRUSH_TOOLS;
		/**
		 * A tag containing all existing fire starting tools such as Flint and Steel.
		 * Fire Charge is not a tool (no durability) and thus, does not go in this tag.
		 * Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_IGNITER = ConventionalItemTags.IGNITER_TOOLS;
		/**
		 * A tag containing all existing maces. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_MACE = ConventionalItemTags.MACE_TOOLS;
		/**
		 * A tag containing all existing wrenches. Do not use this tag for determining a tool's behavior.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> TOOLS_WRENCH = tag("tools/wrench");
		/**
		 * A tag containing melee-based weapons for recipes and loot tables.
		 * Tools are considered melee if they are intentionally intended to be used for melee attack as a primary purpose.
		 * (In other words, Pickaxes are not melee weapons as they are not intended to be a weapon as a primary purpose)
		 * Do not use this tag for determining a tool's behavior in-code.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> MELEE_WEAPON_TOOLS = ConventionalItemTags.MELEE_WEAPON_TOOLS;
		/**
		 * A tag containing ranged-based weapons for recipes and loot tables.
		 * Tools are considered ranged if they can damage entities beyond the weapon's and player's melee attack range.
		 * Do not use this tag for determining a tool's behavior in-code.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> RANGED_WEAPON_TOOLS = ConventionalItemTags.RANGED_WEAPON_TOOLS;
		/**
		 * A tag containing mining-based tools for recipes and loot tables.
		 * Do not use this tag for determining a tool's behavior in-code.
		 * Please use {@link ItemAbilities} instead for what action a tool can do.
		 *
		 * @see ItemAbility
		 * @see ItemAbilities
		 */
		public static final TagKey<Item> MINING_TOOL_TOOLS = ConventionalItemTags.MINING_TOOL_TOOLS;
		/**
		 * Collects the 4 vanilla armor tags into one parent collection for ease.
		 */
		public static final TagKey<Item> ARMORS = ConventionalItemTags.ARMORS;
		/**
		 * Collects the many enchantable tags into one parent collection for ease.
		 */
		public static final TagKey<Item> ENCHANTABLES = ConventionalItemTags.ENCHANTABLES;

		private static TagKey<Item> tag(String name) {
			return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
		}

		private static TagKey<Item> neoforgeTag(String name) {
			return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("neoforge", name));
		}
	}

	/**
	 * Note, fluid tags should not be plural to match the vanilla standard.
	 * This is the only tag category exempted from many-different-types plural rule.
	 */
	public static class Fluids {
		/**
		 * Holds all fluids related to water.<p>
		 * This tag is done to help out multi-loader mods/datapacks where the vanilla water tag has attached behaviors outside Neo.
		 */
		public static final TagKey<Fluid> WATER = ConventionalFluidTags.WATER;
		/**
		 * Holds all fluids related to lava.<p>
		 * This tag is done to help out multi-loader mods/datapacks where the vanilla lava tag has attached behaviors outside Neo.
		 */
		public static final TagKey<Fluid> LAVA = ConventionalFluidTags.LAVA;
		/**
		 * Holds all fluids related to milk.
		 */
		public static final TagKey<Fluid> MILK = ConventionalFluidTags.MILK;
		/**
		 * Holds all fluids that are gaseous at room temperature.
		 */
		public static final TagKey<Fluid> GASEOUS = ConventionalFluidTags.GASEOUS;
		/**
		 * Holds all fluids related to honey.
		 * <p>
		 * (Standard unit for honey bottle is 250mb per bottle)
		 */
		public static final TagKey<Fluid> HONEY = ConventionalFluidTags.HONEY;
		/**
		 * Holds all fluids related to experience.
		 *
		 * <p>(Standard unit for experience is 810 droplet per 1 experience. However, extraction from Bottle o' Enchanting should yield 27000 droplets while smashing yields less)
		 */
		public static final TagKey<Fluid> EXPERIENCE = ConventionalFluidTags.EXPERIENCE;
		/**
		 * Holds all fluids related to potions. The effects of the potion fluid should be read from DataComponents.
		 * The effects and color of the potion fluid should be read from {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}
		 * component that people should be attaching to the fluidstack of this fluid.
		 * <p>
		 * (Standard unit for potions is {@link net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants#BOTTLE}/27000 droplets per bottle)
		 */
		public static final TagKey<Fluid> POTION = ConventionalFluidTags.POTION;
		/**
		 * Holds all fluids related to Suspicious Stew.
		 * The effects of the suspicious stew fluid should be read from {@link net.minecraft.core.component.DataComponents#SUSPICIOUS_STEW_EFFECTS}
		 * component that people should be attaching to the fluidstack of this fluid.
		 * <p>
		 * (Standard unit for suspicious stew is 250mb per bowl)
		 */
		public static final TagKey<Fluid> SUSPICIOUS_STEW = ConventionalFluidTags.SUSPICIOUS_STEW;
		/**
		 * Holds all fluids related to Mushroom Stew.
		 * <p>
		 * (Standard unit for mushroom stew is 250mb per bowl)
		 */
		public static final TagKey<Fluid> MUSHROOM_STEW = ConventionalFluidTags.MUSHROOM_STEW;
		/**
		 * Holds all fluids related to Rabbit Stew.
		 * <p>
		 * (Standard unit for rabbit stew is 250mb per bowl)
		 */
		public static final TagKey<Fluid> RABBIT_STEW = ConventionalFluidTags.RABBIT_STEW;
		/**
		 * Holds all fluids related to Beetroot Soup.
		 * <p>
		 * (Standard unit for beetroot soup is 250mb per bowl)
		 */
		public static final TagKey<Fluid> BEETROOT_SOUP = ConventionalFluidTags.BEETROOT_SOUP;
		/**
		 * Tag that holds all fluids that recipe viewers should not show to users.
		 */
		public static final TagKey<Fluid> HIDDEN_FROM_RECIPE_VIEWERS = ConventionalFluidTags.HIDDEN_FROM_RECIPE_VIEWERS;

		private static TagKey<Fluid> tag(String name) {
			return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", name));
		}
	}

	public static class Enchantments {
		/**
		 * A tag containing enchantments that increase the amount or
		 * quality of drops from blocks, such as {@link net.minecraft.world.item.enchantment.Enchantments#FORTUNE}.
		 */
		public static final TagKey<Enchantment> INCREASE_BLOCK_DROPS = ConventionalEnchantmentTags.INCREASE_BLOCK_DROPS;
		/**
		 * A tag containing enchantments that increase the amount or
		 * quality of drops from entities, such as {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING}.
		 */
		public static final TagKey<Enchantment> INCREASE_ENTITY_DROPS = ConventionalEnchantmentTags.INCREASE_ENTITY_DROPS;
		/**
		 * For enchantments that increase the damage dealt by an item.
		 */
		public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENTS = ConventionalEnchantmentTags.WEAPON_DAMAGE_ENHANCEMENTS;
		/**
		 * For enchantments that increase movement speed for entity wearing armor enchanted with it.
		 */
		public static final TagKey<Enchantment> ENTITY_SPEED_ENHANCEMENTS = ConventionalEnchantmentTags.ENTITY_SPEED_ENHANCEMENTS;
		/**
		 * For enchantments that applies movement-based benefits unrelated to speed for the entity wearing armor enchanted with it.
		 * Example: Reducing falling speeds ({@link net.minecraft.world.item.enchantment.Enchantments#FEATHER_FALLING}) or allowing walking on water ({@link net.minecraft.world.item.enchantment.Enchantments#FROST_WALKER})
		 */
		public static final TagKey<Enchantment> ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS = ConventionalEnchantmentTags.ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS;
		/**
		 * For enchantments that decrease damage taken or otherwise benefit, in regard to damage, the entity wearing armor enchanted with it.
		 */
		public static final TagKey<Enchantment> ENTITY_DEFENSE_ENHANCEMENTS = ConventionalEnchantmentTags.ENTITY_DEFENSE_ENHANCEMENTS;

		private static TagKey<Enchantment> tag(String name) {
			return TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("c", name));
		}
	}

	public static class Biomes {
		/**
		 * For biomes that should not spawn monsters over time the normal way.
		 * In other words, their Spawners and Spawn Cost entries have the monster category empty.
		 * Example: Mushroom Biomes not having Zombies, Creepers, Skeleton, nor any other normal monsters.
		 */
		public static final TagKey<Biome> NO_DEFAULT_MONSTERS = ConventionalBiomeTags.NO_DEFAULT_MONSTERS;
		/**
		 * Biomes that should not be locatable/selectable by modded biome-locating items or abilities.
		 */
		public static final TagKey<Biome> HIDDEN_FROM_LOCATOR_SELECTION = ConventionalBiomeTags.HIDDEN_FROM_LOCATOR_SELECTION;

		public static final TagKey<Biome> IS_VOID = ConventionalBiomeTags.IS_VOID;

		/**
		 * Biomes that are above 0.8 temperature. (Excluding 0.8)
		 */
		public static final TagKey<Biome> IS_HOT = ConventionalBiomeTags.IS_HOT;
		public static final TagKey<Biome> IS_HOT_OVERWORLD = ConventionalBiomeTags.IS_HOT_OVERWORLD;
		public static final TagKey<Biome> IS_HOT_NETHER = ConventionalBiomeTags.IS_HOT_NETHER;
		public static final TagKey<Biome> IS_HOT_END = ConventionalBiomeTags.IS_HOT_END;

		/**
		 * Biomes that are between 0.5 and 0.8 temperature range. (Including 0.5 and 0.8)
		 */
		public static final TagKey<Biome> IS_TEMPERATE = ConventionalBiomeTags.IS_TEMPERATE;
		public static final TagKey<Biome> IS_TEMPERATE_OVERWORLD = ConventionalBiomeTags.IS_TEMPERATE_OVERWORLD;
		public static final TagKey<Biome> IS_TEMPERATE_NETHER = ConventionalBiomeTags.IS_TEMPERATE_NETHER;
		public static final TagKey<Biome> IS_TEMPERATE_END = ConventionalBiomeTags.IS_TEMPERATE_END;

		/**
		 * Biomes that are below 0.5 temperature. (Excluding 0.5)
		 */
		public static final TagKey<Biome> IS_COLD = ConventionalBiomeTags.IS_COLD;
		public static final TagKey<Biome> IS_COLD_OVERWORLD = ConventionalBiomeTags.IS_COLD_OVERWORLD;
		public static final TagKey<Biome> IS_COLD_NETHER = ConventionalBiomeTags.IS_COLD_NETHER;
		public static final TagKey<Biome> IS_COLD_END = ConventionalBiomeTags.IS_COLD_END;

		/**
		 * If a biome has trees but spawn infrequently like a Savanna or Sparse Jungle, then the biome is considered having sparse vegetation. It does NOT mean no trees.
		 */
		public static final TagKey<Biome> IS_SPARSE_VEGETATION = ConventionalBiomeTags.IS_VEGETATION_SPARSE;
		public static final TagKey<Biome> IS_SPARSE_VEGETATION_OVERWORLD = ConventionalBiomeTags.IS_VEGETATION_SPARSE_OVERWORLD;
		public static final TagKey<Biome> IS_SPARSE_VEGETATION_NETHER = ConventionalBiomeTags.IS_VEGETATION_SPARSE_NETHER;
		public static final TagKey<Biome> IS_SPARSE_VEGETATION_END = ConventionalBiomeTags.IS_VEGETATION_SPARSE_END;
		/**
		 * If a biome has more vegetation than a regular Forest biome, then it is considered having dense vegetation.
		 * This is more subjective so simply do your best with classifying your biomes.
		 */
		public static final TagKey<Biome> IS_DENSE_VEGETATION = ConventionalBiomeTags.IS_VEGETATION_DENSE;
		public static final TagKey<Biome> IS_DENSE_VEGETATION_OVERWORLD = ConventionalBiomeTags.IS_VEGETATION_DENSE_OVERWORLD;
		public static final TagKey<Biome> IS_DENSE_VEGETATION_NETHER = ConventionalBiomeTags.IS_VEGETATION_DENSE_NETHER;
		public static final TagKey<Biome> IS_DENSE_VEGETATION_END = ConventionalBiomeTags.IS_VEGETATION_DENSE_END;

		public static final TagKey<Biome> IS_WET = ConventionalBiomeTags.IS_WET;
		public static final TagKey<Biome> IS_WET_OVERWORLD = ConventionalBiomeTags.IS_WET_OVERWORLD;
		public static final TagKey<Biome> IS_WET_NETHER = ConventionalBiomeTags.IS_WET_NETHER;
		public static final TagKey<Biome> IS_WET_END = ConventionalBiomeTags.IS_WET_END;
		public static final TagKey<Biome> IS_DRY = ConventionalBiomeTags.IS_DRY;
		public static final TagKey<Biome> IS_DRY_OVERWORLD = ConventionalBiomeTags.IS_DRY_OVERWORLD;
		public static final TagKey<Biome> IS_DRY_NETHER = ConventionalBiomeTags.IS_DRY_NETHER;
		public static final TagKey<Biome> IS_DRY_END = ConventionalBiomeTags.IS_DRY_END;

		/**
		 * Biomes that spawn in the Overworld.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_OVERWORLD}
		 * <p>
		 * NOTE: If you do not add to the vanilla Overworld tag, be sure to add to
		 * {@link net.minecraft.tags.BiomeTags#HAS_STRONGHOLD} so some Strongholds do not go missing.)
		 */
		public static final TagKey<Biome> IS_OVERWORLD = ConventionalBiomeTags.IS_OVERWORLD;

		public static final TagKey<Biome> IS_CONIFEROUS_TREE = ConventionalBiomeTags.IS_CONIFEROUS_TREE;
		public static final TagKey<Biome> IS_SAVANNA_TREE = ConventionalBiomeTags.IS_SAVANNA_TREE;
		public static final TagKey<Biome> IS_JUNGLE_TREE = ConventionalBiomeTags.IS_JUNGLE_TREE;
		public static final TagKey<Biome> IS_DECIDUOUS_TREE = ConventionalBiomeTags.IS_DECIDUOUS_TREE;

		/**
		 * Biomes that spawn as part of giant mountains.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_MOUNTAIN})
		 */
		public static final TagKey<Biome> IS_MOUNTAIN = ConventionalBiomeTags.IS_MOUNTAIN;
		public static final TagKey<Biome> IS_MOUNTAIN_PEAK = ConventionalBiomeTags.IS_MOUNTAIN_PEAK;
		public static final TagKey<Biome> IS_MOUNTAIN_SLOPE = ConventionalBiomeTags.IS_MOUNTAIN_SLOPE;

		/**
		 * For temperate or warmer plains-like biomes.
		 * For snowy plains-like biomes, see {@link #IS_SNOWY_PLAINS}.
		 */
		public static final TagKey<Biome> IS_PLAINS = ConventionalBiomeTags.IS_PLAINS;
		/**
		 * For snowy plains-like biomes.
		 * For warmer plains-like biomes, see {@link #IS_PLAINS}.
		 */
		public static final TagKey<Biome> IS_SNOWY_PLAINS = ConventionalBiomeTags.IS_SNOWY_PLAINS;
		/**
		 * Biomes densely populated with deciduous trees.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_FOREST})
		 */
		public static final TagKey<Biome> IS_FOREST = ConventionalBiomeTags.IS_FOREST;
		public static final TagKey<Biome> IS_BIRCH_FOREST = ConventionalBiomeTags.IS_BIRCH_FOREST;
		public static final TagKey<Biome> IS_FLOWER_FOREST = ConventionalBiomeTags.IS_FLOWER_FOREST;
		/**
		 * Biomes that spawn as a taiga.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_TAIGA})
		 */
		public static final TagKey<Biome> IS_TAIGA = ConventionalBiomeTags.IS_TAIGA;
		public static final TagKey<Biome> IS_OLD_GROWTH = ConventionalBiomeTags.IS_OLD_GROWTH;
		/**
		 * Biomes that spawn as a hills biome. (Previously was called Extreme Hills biome in past)
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_HILL})
		 */
		public static final TagKey<Biome> IS_HILL = ConventionalBiomeTags.IS_HILL;
		public static final TagKey<Biome> IS_WINDSWEPT = ConventionalBiomeTags.IS_WINDSWEPT;
		/**
		 * Biomes that spawn as a jungle.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_JUNGLE})
		 */
		public static final TagKey<Biome> IS_JUNGLE = ConventionalBiomeTags.IS_JUNGLE;
		/**
		 * Biomes that spawn as a savanna.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_SAVANNA})
		 */
		public static final TagKey<Biome> IS_SAVANNA = ConventionalBiomeTags.IS_SAVANNA;
		public static final TagKey<Biome> IS_SWAMP = ConventionalBiomeTags.IS_SWAMP;
		public static final TagKey<Biome> IS_DESERT = ConventionalBiomeTags.IS_DESERT;
		/**
		 * Biomes that spawn as a badlands.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_BADLANDS})
		 */
		public static final TagKey<Biome> IS_BADLANDS = ConventionalBiomeTags.IS_BADLANDS;
		/**
		 * Biomes that are dedicated to spawning on the shoreline of a body of water.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_BEACH})
		 */
		public static final TagKey<Biome> IS_BEACH = ConventionalBiomeTags.IS_BEACH;
		public static final TagKey<Biome> IS_STONY_SHORES = ConventionalBiomeTags.IS_STONY_SHORES;
		public static final TagKey<Biome> IS_MUSHROOM = ConventionalBiomeTags.IS_MUSHROOM;

		/**
		 * Biomes that spawn as a river.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_RIVER})
		 */
		public static final TagKey<Biome> IS_RIVER = ConventionalBiomeTags.IS_RIVER;
		/**
		 * Biomes that spawn as part of the world's oceans.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_OCEAN})
		 */
		public static final TagKey<Biome> IS_OCEAN = ConventionalBiomeTags.IS_OCEAN;
		/**
		 * Biomes that spawn as part of the world's oceans that have low depth.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_DEEP_OCEAN})
		 */
		public static final TagKey<Biome> IS_DEEP_OCEAN = ConventionalBiomeTags.IS_DEEP_OCEAN;
		public static final TagKey<Biome> IS_SHALLOW_OCEAN = ConventionalBiomeTags.IS_SHALLOW_OCEAN;

		public static final TagKey<Biome> IS_UNDERGROUND = ConventionalBiomeTags.IS_UNDERGROUND;
		public static final TagKey<Biome> IS_CAVE = ConventionalBiomeTags.IS_CAVE;

		/**
		 * Biomes whose flora primarily consists of vibrant thick vegetation and pools of water. Think of Lush Caves as an example.
		 */
		public static final TagKey<Biome> IS_LUSH = ConventionalBiomeTags.IS_LUSH;
		/**
		 * Biomes whose theme revolves around magic. Like a forest full of fairies or plants of magical abilities.
		 */
		public static final TagKey<Biome> IS_MAGICAL = ConventionalBiomeTags.IS_MAGICAL;
		/**
		 * Intended for biomes that spawns infrequently and can be difficult to find.
		 */
		public static final TagKey<Biome> IS_RARE = ConventionalBiomeTags.IS_RARE;
		/**
		 * Biomes that spawn as a flat-topped hill often.
		 */
		public static final TagKey<Biome> IS_PLATEAU = ConventionalBiomeTags.IS_PLATEAU;
		/**
		 * For biomes that are intended to be creepy or scary. For example, see Deep Dark biome or Dark Forest biome.
		 */
		public static final TagKey<Biome> IS_SPOOKY = ConventionalBiomeTags.IS_SPOOKY;
		/**
		 * Biomes that lack any natural life or vegetation.
		 * (Example, land destroyed and sterilized by nuclear weapons)
		 */
		public static final TagKey<Biome> IS_WASTELAND = ConventionalBiomeTags.IS_WASTELAND;
		/**
		 * Biomes whose flora primarily consists of dead or decaying vegetation.
		 */
		public static final TagKey<Biome> IS_DEAD = ConventionalBiomeTags.IS_DEAD;
		/**
		 * Biomes with a large amount of flowers.
		 */
		public static final TagKey<Biome> IS_FLORAL = ConventionalBiomeTags.IS_FLORAL;
		/**
		 * Biomes that are able to spawn sand-based blocks on the surface.
		 */
		public static final TagKey<Biome> IS_SANDY = ConventionalBiomeTags.IS_SANDY;
		/**
		 * For biomes that contains lots of naturally spawned snow.
		 * For biomes where lot of ice is present, see {@link #IS_ICY}.
		 * Biome with lots of both snow and ice may be in both tags.
		 */
		public static final TagKey<Biome> IS_SNOWY = ConventionalBiomeTags.IS_SNOWY;
		/**
		 * For land biomes where ice naturally spawns.
		 * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
		 */
		public static final TagKey<Biome> IS_ICY = ConventionalBiomeTags.IS_ICY;
		/**
		 * Biomes consisting primarily of water.
		 */
		public static final TagKey<Biome> IS_AQUATIC = ConventionalBiomeTags.IS_AQUATIC;
		/**
		 * For water biomes where ice naturally spawns.
		 * For biomes where snow alone spawns, see {@link #IS_SNOWY}.
		 */
		public static final TagKey<Biome> IS_AQUATIC_ICY = ConventionalBiomeTags.IS_AQUATIC_ICY;

		/**
		 * Biomes that spawn in the Nether.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_NETHER})
		 */
		public static final TagKey<Biome> IS_NETHER = ConventionalBiomeTags.IS_NETHER;
		public static final TagKey<Biome> IS_NETHER_FOREST = ConventionalBiomeTags.IS_NETHER_FOREST;

		/**
		 * Biomes that spawn in the End.
		 * (This is for people who want to tag their biomes without getting
		 * side effects from {@link net.minecraft.tags.BiomeTags#IS_END})
		 */
		public static final TagKey<Biome> IS_END = ConventionalBiomeTags.IS_END;
		/**
		 * Biomes that spawn as part of the large islands outside the center island in The End dimension.
		 */
		public static final TagKey<Biome> IS_OUTER_END_ISLAND = ConventionalBiomeTags.IS_OUTER_END_ISLAND;

		/**
		 * Old legacy tag that lost it's intended use case and is too unclear with regard to the current worldgen biome system today.
		 * TODO: remove in 1.22
		 */
		@Deprecated(forRemoval = true, since = "21.1")
		public static final TagKey<Biome> IS_MODIFIED = tag("is_modified");

		private static TagKey<Biome> tag(String name) {
			return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", name));
		}
	}

	public static class Structures {
		/**
		 * Structures that should not show up on minimaps or world map views from mods/sites.
		 * No effect on vanilla map items.
		 */
		public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = ConventionalStructureTags.HIDDEN_FROM_DISPLAYERS;

		/**
		 * Structures that should not be locatable/selectable by modded structure-locating items or abilities.
		 * No effect on vanilla map items.
		 */
		public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = ConventionalStructureTags.HIDDEN_FROM_LOCATOR_SELECTION;

		private static TagKey<Structure> tag(String name) {
			return TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("c", name));
		}
	}

	public static class DamageTypes {
		/**
		 * Damage types representing magic damage.
		 */
		public static final TagKey<DamageType> IS_MAGIC = neoforgeTag("is_magic");

		/**
		 * Damage types representing poison damage.
		 */
		public static final TagKey<DamageType> IS_POISON = neoforgeTag("is_poison");

		/**
		 * Damage types representing damage that can be attributed to withering or the wither.
		 */
		public static final TagKey<DamageType> IS_WITHER = neoforgeTag("is_wither");

		/**
		 * Damage types representing environmental damage, such as fire, lava, magma, cactus, lightning, etc.
		 */
		public static final TagKey<DamageType> IS_ENVIRONMENT = neoforgeTag("is_environment");

		/**
		 * Damage types representing physical damage.<br>
		 * These are types that do not fit other #is_x tags (except #is_fall)
		 * and would meet the general definition of physical damage.
		 */
		public static final TagKey<DamageType> IS_PHYSICAL = neoforgeTag("is_physical");

		/**
		 * Damage types representing damage from commands or other non-gameplay sources.<br>
		 * Damage from these types should not be reduced, and bypasses invulnerability.
		 */
		public static final TagKey<DamageType> IS_TECHNICAL = neoforgeTag("is_technical");

		/**
		 * Damage types that will not cause the red flashing effect.<br>
		 * This tag is empty by default.
		 *
		 * @see net.minecraft.client.renderer.GameRenderer#bobHurt
		 */
		public static final TagKey<DamageType> NO_FLINCH = neoforgeTag("no_flinch");

		private static TagKey<DamageType> neoforgeTag(String name) {
			return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("neoforge", name));
		}
	}

	/**
	 * Use this to get a TagKey's translation key safely on any side.
	 *
	 * @return the translation key for a TagKey.
	 */
	public static String getTagTranslationKey(TagKey<?> tagKey) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("tag.");

		ResourceLocation registryIdentifier = tagKey.registry().location();
		ResourceLocation tagIdentifier = tagKey.location();

		stringBuilder.append(registryIdentifier.toShortLanguageKey().replace("/", "."))
				.append(".")
				.append(tagIdentifier.getNamespace())
				.append(".")
				.append(tagIdentifier.getPath().replace("/", "."));

		return stringBuilder.toString();
	}
}
