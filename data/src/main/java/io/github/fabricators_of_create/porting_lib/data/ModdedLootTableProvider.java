package io.github.fabricators_of_create.porting_lib.data;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataResolver;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ModdedLootTableProvider extends LootTableProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Set<ResourceLocation> requiredTables;
	private final List<LootTableProvider.SubProviderEntry> subProviders;

	public ModdedLootTableProvider(PackOutput packOutput, Set<ResourceLocation> requiredTables, List<SubProviderEntry> subProviders) {
		super(packOutput, requiredTables, subProviders);
		this.requiredTables = requiredTables;
		this.subProviders = subProviders;
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
				return (T)(p_279283_.type() == LootDataType.TABLE ? map.get(p_279283_.location()) : null);
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
				return DataProvider.saveStable(pOutput, LootTable.CODEC, loottable, path);
			}).toArray(CompletableFuture[]::new));
		}
	}

	public List<LootTableProvider.SubProviderEntry> getTables() {
		return this.subProviders;
	}

	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
		for(ResourceLocation resourcelocation : Sets.difference(this.requiredTables, map.keySet())) {
			validationcontext.reportProblem("Missing built-in table: " + resourcelocation);
		}

		map.forEach((id, lootTable) -> {
			lootTable.validate(validationcontext.setParams(lootTable.getParamSet()).enterElement("{" + id + "}", new LootDataId<>(LootDataType.TABLE, id)));
		});
	}

	public String getName() {
		return "LootTables";
	}
}
