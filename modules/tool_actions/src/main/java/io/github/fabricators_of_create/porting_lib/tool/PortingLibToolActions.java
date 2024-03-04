package io.github.fabricators_of_create.porting_lib.tool;

import io.github.fabricators_of_create.porting_lib.tool.loot.CanToolPerformAction;
import io.github.fabricators_of_create.porting_lib.tool.mixin.BuilderAccessor;
import io.github.fabricators_of_create.porting_lib.tool.mixin.InvertedLootItemConditionAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.List;
import java.util.Set;

public class PortingLibToolActions implements ModInitializer {
	@Override
	public void onInitialize() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			findAndReplaceInLootTableBuilder(tableBuilder, Items.SHEARS, ToolActions.SHEARS_DIG);
		});
	}

	private void findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, ToolAction toolAction) {

		builder.modifyPools(lootPool -> {
			findAndReplaceInLootPool(lootPool, from, toolAction);
		});
	}

	private boolean findAndReplaceInLootPool(LootPool.Builder lootPool, Item from, ToolAction toolAction) {
		List<LootPoolEntryContainer> lootEntries = lootPool.entries;
		List<LootItemCondition> lootConditions = lootPool.conditions;
		boolean found = false;

		for (LootPoolEntryContainer lootEntry : lootEntries) {
			if (findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
				found = true;
			}
			if (lootEntry instanceof CompositeEntryBase) {
				if (findAndReplaceInParentedLootEntry((CompositeEntryBase) lootEntry, from, toolAction)) {
					found = true;
				}
			}
		}

		if (lootConditions == null) {
			throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "4_");
		}

		for (int i = 0; i < lootConditions.size(); i++) {
			LootItemCondition lootCondition = lootConditions.get(i);
			if (lootCondition instanceof MatchTool && checkMatchTool((MatchTool) lootCondition, from)) {
				lootConditions.set(i, CanToolPerformAction.canToolPerformAction(toolAction).build());
				found = true;
			} else if (lootCondition instanceof InvertedLootItemCondition) {
				LootItemCondition invLootCondition = ((InvertedLootItemConditionAccessor) lootCondition).getTerm();

				if (invLootCondition instanceof MatchTool && checkMatchTool((MatchTool) invLootCondition, from)) {
					lootConditions.set(i, InvertedLootItemCondition.invert(CanToolPerformAction.canToolPerformAction(toolAction)).build());
					found = true;
				} else if (invLootCondition instanceof CompositeLootItemCondition compositeLootItemCondition && findAndReplaceInComposite(compositeLootItemCondition, from, toolAction)) {
					found = true;
				}
			}
		}

		return found;
	}

	private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Item from, ToolAction toolAction) {
		LootPoolEntryContainer[] lootEntries = entry.children;
		boolean found = false;

		if (lootEntries == null) {
			throw new IllegalStateException(CompositeEntryBase.class.getName() + " is missing field f_7942" + "8_");
		}

		for (LootPoolEntryContainer lootEntry : lootEntries) {
			if (findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
				found = true;
			}
		}

		return found;
	}

	private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Item from, ToolAction toolAction) {
		LootItemCondition[] lootConditions = entry.conditions;
		boolean found = false;

		if (lootConditions == null) {
			throw new IllegalStateException(LootPoolEntryContainer.class.getName() + " is missing field f_7963" + "6_");
		}

		for (int i = 0; i < lootConditions.length; i++) {
			if (lootConditions[i] instanceof CompositeLootItemCondition composite && findAndReplaceInComposite(composite, from, toolAction)) {
				found = true;
			} else if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
				lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
				found = true;
			}
		}

		return found;
	}

	private boolean findAndReplaceInComposite(CompositeLootItemCondition alternative, Item from, ToolAction toolAction) {
		LootItemCondition[] lootConditions = alternative.terms;
		boolean found = false;

		if (lootConditions == null) {
			throw new IllegalStateException(CompositeLootItemCondition.class.getName() + " is missing field f_28560" + "9_");
		}

		for (int i = 0; i < lootConditions.length; i++) {
			if (lootConditions[i] instanceof MatchTool && checkMatchTool((MatchTool) lootConditions[i], from)) {
				lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
				found = true;
			}
		}

		return found;
	}

	private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
		ItemPredicate predicate = lootCondition.predicate;
		Set<Item> items = predicate.items;
		return items != null && items.contains(expected);
	}
}
