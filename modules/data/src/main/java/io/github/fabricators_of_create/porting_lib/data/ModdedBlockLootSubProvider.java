package io.github.fabricators_of_create.porting_lib.data;

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
		Set<ResourceKey<LootTable>> set = Sets.newHashSet();

		for(Block block : getKnownBlocks()) {
			ResourceKey<LootTable> resourceKey = block.getLootTable();
			if (resourceKey != BuiltInLootTables.EMPTY && set.add(resourceKey)) {
				LootTable.Builder builder6 = map.remove(resourceKey);
				if (builder6 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceKey, BuiltInRegistries.BLOCK.getKey(block)));
				}

				biConsumer.accept(resourceKey, builder6);
			}
		}

		if (!map.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + map.keySet());
		}
	}

	protected Iterable<Block> getKnownBlocks() {
		return BuiltInRegistries.BLOCK;
	}
}
