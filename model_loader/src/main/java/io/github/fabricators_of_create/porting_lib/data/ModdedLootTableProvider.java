package io.github.fabricators_of_create.porting_lib.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.data.loot.packs.VanillaGiftLoot;
import net.minecraft.data.loot.packs.VanillaPiglinBarterLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
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
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		Map<ResourceLocation, LootTable> map = Maps.newHashMap();
		this.getTables().forEach((subProviderEntry) -> {
			subProviderEntry.provider().get().generate((resourceLocation, builder) -> {
				if (map.put(resourceLocation, builder.setParamSet(subProviderEntry.paramSet()).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + resourceLocation);
				}
			});
		});
		LootContextParamSet var10002 = LootContextParamSets.ALL_PARAMS;
		Function var10003 = (resourceLocationx) -> {
			return null;
		};
		Objects.requireNonNull(map);
		ValidationContext validationContext = new ValidationContext(var10002, var10003, map::get);
		Set<ResourceLocation> set = Sets.difference(this.requiredTables, map.keySet());
		Iterator var5 = set.iterator();

		validate(map, validationContext);

		Multimap<String, String> multimap = validationContext.getProblems();
		if (!multimap.isEmpty()) {
			multimap.forEach((string, string2) -> {
				LOGGER.warn("Found validation problem in {}: {}", string, string2);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			return CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map((entry) -> {
				ResourceLocation resourceLocation = (ResourceLocation)entry.getKey();
				LootTable lootTable = (LootTable)entry.getValue();
				Path path = this.pathProvider.json(resourceLocation);
				return DataProvider.saveStable(cachedOutput, LootTables.serialize(lootTable), path);
			}).toArray((i) -> {
				return new CompletableFuture[i];
			}));
		}
	}

	protected List<LootTableProvider.SubProviderEntry> getTables() {
		return subProviders;
	}

	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		for(ResourceLocation resourcelocation : Sets.difference(this.requiredTables, map.keySet())) {
			validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
		}

		map.forEach((resourceLocationx, lootTable) -> {
			LootTables.validate(validationtracker, resourceLocationx, lootTable);
		});
	}

	public String getName() {
		return "LootTables";
	}
}
