package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.Util;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class ModdedLootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final PackOutput.PathProvider pathProvider;
	private final Set<ResourceKey<LootTable>> requiredTables;
	private final List<LootTableProvider.SubProviderEntry> subProviders;
	private final CompletableFuture<HolderLookup.Provider> registries;

	public ModdedLootTableProvider(
			PackOutput output,
			Set<ResourceKey<LootTable>> requiredTables,
			List<LootTableProvider.SubProviderEntry> subProviders,
			CompletableFuture<HolderLookup.Provider> registries
	) {
		this.pathProvider = output.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
		this.subProviders = subProviders;
		this.requiredTables = requiredTables;
		this.registries = registries;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.registries.thenCompose(provider -> this.run(output, provider));
	}

	private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
		WritableRegistry<LootTable> writableregistry = new MappedRegistry<>(Registries.LOOT_TABLE, Lifecycle.experimental());
		Map<RandomSupport.Seed128bit, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
		getTables().forEach(entry -> entry.provider().apply(provider).generate((key, builder) -> {
			ResourceLocation resourcelocation = sequenceIdForLootTable(key);
			ResourceLocation resourcelocation1 = map.put(RandomSequence.seedForKey(resourcelocation), resourcelocation);
			if (resourcelocation1 != null) {
				Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + resourcelocation1 + " and " + key.location());
			}

			builder.setRandomSequence(resourcelocation);
			LootTable loottable = builder.setParamSet(entry.paramSet()).build();
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
				ResourceKey<LootTable> key = entry.getKey();
				LootTable loottable = entry.getValue();
				Path path = this.pathProvider.json(key.location());
				return DataProvider.saveStable(output, provider, LootTable.DIRECT_CODEC, loottable, path);
			}).toArray(CompletableFuture[]::new));
		}
	}

	public List<LootTableProvider.SubProviderEntry> getTables() {
		return this.subProviders;
	}

	protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
		for(ResourceKey<LootTable> resourcekey : Sets.difference(this.requiredTables, writableregistry.registryKeySet())) {
			problemreporter$collector.report("Missing built-in table: " + resourcekey.location());
		}

		writableregistry.holders().forEach(holder -> {
			holder.value().validate(validationcontext.setParams(holder.value().getParamSet()).enterElement("{" + holder.key().location() + "}", holder.key()));
		});
	}

	private static ResourceLocation sequenceIdForLootTable(ResourceKey<LootTable> tableKey) {
		return tableKey.location();
	}

	@Override
	public final String getName() {
		return "Loot Tables";
	}
}
