package io.github.fabricators_of_create.porting_lib.tool.data;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Either;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.serialization.Lifecycle;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.loot.CanItemPerformAbility;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.jetbrains.annotations.Nullable;

/**
 * Currently used only for replacing shears item to shears_dig item ability
 */
public final class ItemAbilityLootTableProvider extends LootTableProvider {
	private final List<Function<LootItemCondition, LootItemCondition.Builder>> conditionReplacers = new ArrayList<>();

	public ItemAbilityLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
		super(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput, provider).subProviders, provider);
	}

	protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
		// Do not validate against all registered loot tables
	}

	public List<LootTableProvider.SubProviderEntry> getTables() {
		replaceLootItemCondition(condition -> {
			if (condition instanceof MatchTool matchTool && checkMatchTool(matchTool, Items.SHEARS)) {
				return CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_DIG);
			}
			return null;
		});
		return this.getTables().stream().map(entry -> {
			// Provides new sub provider with filtering only changed loot tables and replacing condition item to condition tag
			return new LootTableProvider.SubProviderEntry((provider) -> replaceAndFilterChangesOnly(entry.provider().apply(provider)), entry.paramSet());
		}).collect(Collectors.toList());
	}

	private LootTableSubProvider replaceAndFilterChangesOnly(LootTableSubProvider subProvider) {
		return (newConsumer) -> subProvider.generate((resourceLocation, builder) -> {
			LootTable.Builder newBuilder = findAndReplaceInLootTableBuilder(builder);
			if (newBuilder != null) {
				newConsumer.accept(resourceLocation, newBuilder);
			}
		});
	}

	private void replaceLootItemCondition(Function<LootItemCondition, LootItemCondition.Builder> replacer) {
		conditionReplacers.add(replacer);
	}

	@Nullable
	private LootTable.Builder findAndReplaceInLootTableBuilder(LootTable.Builder builder) {
		LootTable lootTable = builder.build();

		Optional<ResourceLocation> randomSequence = getPrivateValue(LootTable.class, lootTable, "randomSequence");
		List<LootPool> lootPools = getPrivateValue(LootTable.class, lootTable, "pools");
		List<LootItemFunction> lootItemFunctions = getPrivateValue(LootTable.class, lootTable, "functions");

		boolean found = false;
		LootTable.Builder newBuilder = new LootTable.Builder();
		newBuilder.setParamSet(lootTable.getParamSet());
		randomSequence.ifPresent(newBuilder::setRandomSequence);
		for (LootItemFunction lootItemFunction : lootItemFunctions) {
			newBuilder.apply(() -> lootItemFunction);
		}

		for (LootPool lootPool : lootPools) {
			found |= findAndReplaceInLootPool(lootPool, newBuilder);
		}

		return found ? newBuilder : null;
	}

	private boolean findAndReplaceInLootPool(LootPool lootPool, LootTable.Builder newBuilder) {
		List<LootPoolEntryContainer> lootEntries = getPrivateValue(LootPool.class, lootPool, "entries");
		List<LootItemCondition> lootConditions = getPrivateValue(LootPool.class, lootPool, "conditions");
		List<LootItemFunction> lootFunctions = getPrivateValue(LootPool.class, lootPool, "functions");

		LootPool.Builder poolBuilder = new LootPool.Builder();
		poolBuilder.setRolls(lootPool.rolls);
		poolBuilder.setBonusRolls(lootPool.bonusRolls);
//		poolBuilder.name(lootPool.getName());
		boolean found = false;

		for (LootPoolEntryContainer lootEntry : lootEntries) {
			found |= findAndReplaceInLootEntry(lootEntry, poolBuilder::add);
		}

		for (LootItemCondition lootCondition : lootConditions) {
			if (lootCondition instanceof InvertedLootItemCondition invertedLootItemCondition) {
				LootItemCondition invLootCondition = invertedLootItemCondition.term();

				Consumer<LootItemCondition.Builder> consumer = cond -> poolBuilder.when(InvertedLootItemCondition.invert(cond));
				if (invLootCondition instanceof CompositeLootItemCondition compositeLootItemCondition && findAndReplaceInComposite(compositeLootItemCondition, consumer)) {
					found = true;
				} else {
					found |= replaceCondition(invLootCondition, consumer);
				}
			} else {
				found |= replaceCondition(lootCondition, poolBuilder::when);
			}
		}

		for (LootItemFunction lootFunction : lootFunctions) {
			poolBuilder.apply(() -> lootFunction);
		}

		newBuilder.withPool(poolBuilder);

		return found;
	}

	private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Consumer<LootPoolEntryContainer.Builder<?>> newBuilder) {
		List<LootPoolEntryContainer> lootEntries = getPrivateValue(CompositeEntryBase.class, entry, "children");

		boolean found = false;
		for (LootPoolEntryContainer lootEntry : lootEntries) {
			found |= findAndReplaceInLootEntry(lootEntry, newBuilder);
		}

		return found;
	}

	private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Consumer<LootPoolEntryContainer.Builder<?>> newBuilder) {
		List<LootItemCondition> lootConditions = getPrivateValue(LootPoolEntryContainer.class, entry, "conditions");

		boolean found = false;

		LootPoolEntryContainer.Builder<?> builder;
		if (entry instanceof CompositeEntryBase compositeEntryBase) {
			Consumer<LootPoolEntryContainer.Builder<?>> consumer;
			if (compositeEntryBase instanceof AlternativesEntry) {
				builder = new AlternativesEntry.Builder();
				consumer = builder::otherwise;
			} else if (compositeEntryBase instanceof SequentialEntry) {
				builder = new SequentialEntry.Builder();
				consumer = builder::then;
			} else if (compositeEntryBase instanceof EntryGroup) {
				builder = new EntryGroup.Builder();
				consumer = builder::append;
			} else {
				throw new IllegalStateException("Unknown CompositeEntryBase type: " + compositeEntryBase.getClass().getName());
			}
			found |= findAndReplaceInParentedLootEntry(compositeEntryBase, consumer);
		} else if (entry instanceof LootPoolSingletonContainer singleton) {
			if (singleton instanceof DynamicLoot dynamicLoot) {
				ResourceLocation name = getPrivateValue(DynamicLoot.class, dynamicLoot, "name");
				builder = DynamicLoot.dynamicEntry(name);
			} else if (singleton instanceof EmptyLootItem) {
				builder = EmptyLootItem.emptyItem();
			} else if (singleton instanceof LootItem lootItem) {
				Holder<Item> item = getPrivateValue(LootItem.class, lootItem, "item");
				builder = LootItem.lootTableItem(item.value());
			} else if (singleton instanceof TagEntry tagEntry) {
				TagKey<Item> tag = getPrivateValue(TagEntry.class, tagEntry, "tag");
				boolean expand = getPrivateValue(TagEntry.class, tagEntry, "expand");
				builder = expand ? TagEntry.expandTag(tag) : TagEntry.tagContents(tag);
			} else if (singleton instanceof NestedLootTable reference) {
				Either<ResourceKey<LootTable>, LootTable> contents = getPrivateValue(NestedLootTable.class, reference, "contents");
				builder = contents.map(NestedLootTable::lootTableReference, NestedLootTable::inlineLootTable);
			} else {
				throw new IllegalStateException("Unknown LootPoolSingletonContainer type: " + singleton.getClass().getName());
			}
			int weight = getPrivateValue(LootPoolSingletonContainer.class, singleton, "weight");
			int quality = getPrivateValue(LootPoolSingletonContainer.class, singleton, "quality");
			List<LootItemFunction> functions = getPrivateValue(LootPoolSingletonContainer.class, singleton, "functions");
			((LootPoolSingletonContainer.Builder<?>) builder).setWeight(weight);
			((LootPoolSingletonContainer.Builder<?>) builder).setQuality(quality);
			for (LootItemFunction function : functions) {
				((LootPoolSingletonContainer.Builder<?>) builder).apply(() -> function);
			}
		} else {
			throw new IllegalStateException("Unknown LootPoolEntryContainer type: " + entry.getClass().getName());
		}

		for (LootItemCondition lootCondition : lootConditions) {
			if (lootCondition instanceof CompositeLootItemCondition composite && findAndReplaceInComposite(composite, builder::when)) {
				found = true;
			} else {
				found |= replaceCondition(lootCondition, builder::when);
			}
		}

		newBuilder.accept(builder);

		return found;
	}

	private boolean findAndReplaceInComposite(CompositeLootItemCondition alternative, Consumer<LootItemCondition.Builder> poolBuilder) {
		List<LootItemCondition> lootConditions = getPrivateValue(CompositeLootItemCondition.class, alternative, "terms");
		CompositeLootItemCondition.Builder builder = alternative instanceof AllOfCondition ? new AllOfCondition.Builder() : alternative instanceof AnyOfCondition ? new AnyOfCondition.Builder() : null;
		boolean found = false;

		for (LootItemCondition lootCondition : lootConditions) {
			found |= replaceCondition(lootCondition, builder::addTerm);
		}

		poolBuilder.accept(builder);

		return found;
	}

	private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
		return lootCondition
				.predicate()
				.flatMap(ItemPredicate::items)
				.filter(holders -> holders.contains(expected.builtInRegistryHolder()))
				.isPresent();
	}

	private boolean replaceCondition(LootItemCondition lootCondition, Consumer<LootItemCondition.Builder> poolBuilder) {
		for (Function<LootItemCondition, LootItemCondition.Builder> conditionReplacer : conditionReplacers) {
			LootItemCondition.Builder newCondition = conditionReplacer.apply(lootCondition);
			if (newCondition != null) {
				poolBuilder.accept(newCondition);
				return true;
			}
		}
		poolBuilder.accept(() -> lootCondition);
		return false;
	}

	private <T, C> T getPrivateValue(Class<C> clazz, C inst, String name) {
		try {
			var field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(inst);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new IllegalStateException(clazz.getName() + " is missing field " + name);
		}
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.registries.thenCompose(p_323117_ -> this.run(output, p_323117_));
	}

	private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
		WritableRegistry<LootTable> writableregistry = new MappedRegistry<>(Registries.LOOT_TABLE, Lifecycle.experimental());
		Map<RandomSupport.Seed128bit, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
		getTables().forEach(subEntry -> subEntry.provider().apply(provider).generate((key, p_335200_) -> {
			ResourceLocation resourcelocation = sequenceIdForLootTable(key);
			ResourceLocation resourcelocation1 = map.put(RandomSequence.seedForKey(resourcelocation), resourcelocation);
			if (resourcelocation1 != null) {
				Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + resourcelocation1 + " and " + key.location());
			}

			p_335200_.setRandomSequence(resourcelocation);
			LootTable loottable = p_335200_.setParamSet(subEntry.paramSet()).build();
			writableregistry.register(key, loottable, RegistrationInfo.BUILT_IN);
		}));
		writableregistry.freeze();
		ProblemReporter.Collector problemreporter$collector = new ProblemReporter.Collector();
		HolderGetter.Provider holdergetter$provider = new RegistryAccess.ImmutableRegistryAccess(List.of(writableregistry)).freeze().asGetterLookup();
		ValidationContext validationcontext = new ValidationContext(problemreporter$collector, LootContextParamSets.ALL_PARAMS, holdergetter$provider);

		validate(writableregistry, validationcontext, problemreporter$collector);

		Multimap<String, String> multimap = problemreporter$collector.get();
		if (!multimap.isEmpty()) {
			multimap.forEach((key, id) -> LOGGER.warn("Found validation problem in {}: {}", key, id));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			return CompletableFuture.allOf(writableregistry.entrySet().stream().map(entry -> {
				ResourceKey<LootTable> resourcekey1 = entry.getKey();
				LootTable loottable = entry.getValue();
				Path path = this.pathProvider.json(resourcekey1.location());
				return DataProvider.saveStable(output, provider, LootTable.DIRECT_CODEC, loottable, path);
			}).toArray(CompletableFuture[]::new));
		}
	}
}
