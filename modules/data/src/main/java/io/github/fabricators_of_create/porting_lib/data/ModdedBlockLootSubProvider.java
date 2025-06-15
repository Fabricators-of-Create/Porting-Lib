package io.github.fabricators_of_create.porting_lib.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Sets;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class ModdedBlockLootSubProvider extends BlockLootSubProvider {
	protected ModdedBlockLootSubProvider(Set<Item> set, FeatureFlagSet featureFlagSet, HolderLookup.Provider provider) {
		super(set, featureFlagSet, provider);
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
		this.generate();
		Set<ResourceKey<LootTable>> set = new HashSet();

		for(Block block : getKnownBlocks()) {
			if (block.isEnabled(this.enabledFeatures)) {
				ResourceKey<LootTable> lootTable = block.getLootTable();
				if (lootTable != BuiltInLootTables.EMPTY && set.add(lootTable)) {
					LootTable.Builder builder = this.map.remove(lootTable);
					if (builder == null) {
						throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", lootTable.location(), BuiltInRegistries.BLOCK.getKey(block)));
					}

					biConsumer.accept(lootTable, builder);
				}
			}
		}

		if (!this.map.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + String.valueOf(this.map.keySet()));
		}
	}

	protected Iterable<Block> getKnownBlocks() {
		return BuiltInRegistries.BLOCK;
	}
}
