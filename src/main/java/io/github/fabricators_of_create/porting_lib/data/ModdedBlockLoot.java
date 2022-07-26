package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Sets;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BlockLootAccessor;
import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.BiConsumer;

public abstract class ModdedBlockLoot extends BlockLoot {
	protected abstract void addTables();

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
		this.addTables();
		Set<ResourceLocation> set = Sets.<ResourceLocation>newHashSet();

		for(Block block : Registry.BLOCK) {
			ResourceLocation resourceLocation = block.getLootTable();
			if (resourceLocation != BuiltInLootTables.EMPTY && set.add(resourceLocation)) {
				LootTable.Builder builder6 = ((BlockLootAccessor)this).getMap().remove(resourceLocation);
				if (builder6 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceLocation, Registry.BLOCK.getKey(block)));
				}

				biConsumer.accept(resourceLocation, builder6);
			}
		}

		if (!((BlockLootAccessor)this).getMap().isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + ((BlockLootAccessor)this).getMap().keySet());
		}
	}

	protected Iterable<Block> getKnownBlocks() {
		return Registry.BLOCK;
	}
}
