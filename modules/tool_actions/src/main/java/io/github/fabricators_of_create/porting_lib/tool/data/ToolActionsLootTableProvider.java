package io.github.fabricators_of_create.porting_lib.tool.data;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib.tool.loot.CanToolPerformAction;
import io.github.fabricators_of_create.porting_lib.tool.mixin.BuilderAccessor;
import io.github.fabricators_of_create.porting_lib.tool.mixin.InvertedLootItemConditionAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataResolver;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

/**
 * Currently used only for replacing shears item to shears_dig tool action
 */
public final class ToolActionsLootTableProvider extends LootTableProvider {
	public ToolActionsLootTableProvider(FabricDataOutput packOutput) {
		super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput).subProviders);
	}

	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
		// Do not validate against all registered loot tables
	}

	public List<LootTableProvider.SubProviderEntry> getTables() {
		return this.subProviders.stream().map(entry -> {
			// Provides new sub provider with filtering only changed loot tables and replacing condition item to condition tag
			return new LootTableProvider.SubProviderEntry(() -> replaceAndFilterChangesOnly(entry.provider().get()), entry.paramSet());
		}).collect(Collectors.toList());
	}

	private LootTableSubProvider replaceAndFilterChangesOnly(LootTableSubProvider subProvider) {
		return newConsumer -> subProvider.generate((resourceLocation, builder) -> {
			if (findAndReplaceInLootTableBuilder(builder, Items.SHEARS, ToolActions.SHEARS_DIG)) {
				newConsumer.accept(resourceLocation, builder);
			}
		});
	}

	private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, ToolAction toolAction) {
		List<LootPool> lootPools = ((BuilderAccessor) builder).getPools();
		boolean found = false;

		for (LootPool lootPool : lootPools) {
			if (findAndReplaceInLootPool(lootPool, from, toolAction)) {
				found = true;
			}
		}

		return found;
	}

	private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, ToolAction toolAction) {
		LootPoolEntryContainer[] lootEntries = lootPool.entries;
		LootItemCondition[] lootConditions = lootPool.conditions;
		boolean found = false;

		if (lootEntries == null) {
			throw new IllegalStateException(LootPool.class.getName() + " is missing field f_7902" + "3_");
		}

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

		for (int i = 0; i < lootConditions.length; i++) {
			LootItemCondition lootCondition = lootConditions[i];
			if (lootCondition instanceof MatchTool && checkMatchTool((MatchTool) lootCondition, from)) {
				lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).build();
				found = true;
			} else if (lootCondition instanceof InvertedLootItemCondition) {
				LootItemCondition invLootCondition = ((InvertedLootItemConditionAccessor) lootCondition).getTerm();

				if (invLootCondition instanceof MatchTool && checkMatchTool((MatchTool) invLootCondition, from)) {
					lootConditions[i] = InvertedLootItemCondition.invert(CanToolPerformAction.canToolPerformAction(toolAction)).build();
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

	@Override
	public CompletableFuture<?> run(CachedOutput pOutput) {
		final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
		Map<RandomSupport.Seed128bit, ResourceLocation> map1 = new Object2ObjectOpenHashMap<>();
		this.getTables().forEach((entry) -> entry.provider().get().generate((key, builder) -> {
			ResourceLocation id = map1.put(RandomSequence.seedForKey(key), key);
			if (id != null) {
				Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + id + " and " + key);
			}

			builder.setRandomSequence(key);
			if (map.put(key, builder.setParamSet(entry.paramSet()).build()) != null) {
				throw new IllegalStateException("Duplicate loot table " + key);
			}
		}));
		ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
			@Nullable
			public <T> T getElement(LootDataId<T> p_279283_) {
				return (T) (p_279283_.type() == LootDataType.TABLE ? map.get(p_279283_.location()) : null);
			}
		});

		validate(map, validationcontext);

		Multimap<String, String> multimap = validationcontext.getProblems();
		if (!multimap.isEmpty()) {
			multimap.forEach((p_124446_, p_124447_) -> {
				LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			return CompletableFuture.allOf(map.entrySet().stream().map((lootTableEntry) -> {
				ResourceLocation lootTableId = lootTableEntry.getKey();
				LootTable loottable = lootTableEntry.getValue();
				Path path = this.pathProvider.json(lootTableId);
				return DataProvider.saveStable(pOutput, LootDataType.TABLE.parser().toJsonTree(loottable), path);
			}).toArray(CompletableFuture[]::new));
		}
	}
}
