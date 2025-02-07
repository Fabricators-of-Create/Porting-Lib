package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Sets;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BlockLootAccessor;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class ModdedBlockLoot extends BlockLootTableGenerator {
	protected abstract void addTables();

	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.addTables();
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for(Block block : getKnownBlocks()) {
			Identifier resourceLocation = block.getLootTableId();
			if (resourceLocation != LootTables.EMPTY && set.add(resourceLocation)) {
				LootTable.Builder builder6 = ((BlockLootAccessor)this).getMap().remove(resourceLocation);
				if (builder6 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceLocation, Registry.BLOCK.getId(block)));
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
