package io.github.fabricators_of_create.porting_lib.data;

import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Sets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class ModdedBlockLootSubProvider extends BlockLootSubProvider {
	protected ModdedBlockLootSubProvider(Set<Item> set, FeatureFlagSet featureFlagSet) {
		super(set, featureFlagSet);
	}

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
		this.generate();
		Set<ResourceLocation> set = Sets.<ResourceLocation>newHashSet();

		for(Block block : getKnownBlocks()) {
			ResourceLocation resourceLocation = block.getLootTable();
			if (resourceLocation != BuiltInLootTables.EMPTY && set.add(resourceLocation)) {
				LootTable.Builder builder6 = map.remove(resourceLocation);
				if (builder6 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceLocation, BuiltInRegistries.BLOCK.getKey(block)));
				}

				biConsumer.accept(resourceLocation, builder6);
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
