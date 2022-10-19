package io.github.fabricators_of_create.porting_lib.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

public class ModdedLootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final PackOutput.PathProvider pathProvider;
	private final List<LootTableProvider.SubProviderEntry> subProviders = ImmutableList.of(new LootTableProvider.SubProviderEntry(VanillaFishingLoot::new, LootContextParamSets.FISHING), new LootTableProvider.SubProviderEntry(VanillaChestLoot::new, LootContextParamSets.CHEST), new LootTableProvider.SubProviderEntry(VanillaEntityLoot::new, LootContextParamSets.ENTITY), new LootTableProvider.SubProviderEntry(VanillaBlockLoot::new, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(VanillaPiglinBarterLoot::new, LootContextParamSets.PIGLIN_BARTER), new LootTableProvider.SubProviderEntry(VanillaGiftLoot::new, LootContextParamSets.GIFT));

	public ModdedLootTableProvider(PackOutput packOutput) {
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
	}

	public void run(CachedOutput p_236269_) {
		Map<ResourceLocation, LootTable> map = Maps.newHashMap();
		getTables().forEach((subProviderEntry) -> {
			subProviderEntry.provider().get().generate((resourceLocation, builder) -> {
				if (map.put(resourceLocation, builder.setParamSet(subProviderEntry.paramSet()).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + resourceLocation);
				}
			});
		});
		ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, (p_124465_) -> {
			return null;
		}, map::get);

		validate(map, validationcontext);

		Multimap<String, String> multimap = validationcontext.getProblems();
		if (!multimap.isEmpty()) {
			multimap.forEach((p_124446_, p_124447_) -> {
				LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((p_236272_, p_236273_) -> {
				Path path = this.pathProvider.json(p_236272_);

				try {
					DataProvider.saveStable(p_236269_, LootTables.serialize(p_236273_), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save loot table {}", path, ioexception);
				}

			});
		}
	}

	protected List<LootTableProvider.SubProviderEntry> getTables() {
		return subProviders;
	}

	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		for(ResourceLocation resourcelocation : Sets.difference(BuiltInLootTables.all(), map.keySet())) {
			validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
		}

		map.forEach((p_218436_2_, p_218436_3_) -> {
			LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
		});
	}

	public String getName() {
		return "LootTables";
	}
}
