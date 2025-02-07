package io.github.fabricators_of_create.porting_lib.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.BarterLootTableGenerator;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.ChestLootTableGenerator;
import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.data.server.FishingLootTableGenerator;
import net.minecraft.data.server.GiftLootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

public class ModdedLootTableProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> subProviders = ImmutableList.of(Pair.of(FishingLootTableGenerator::new, LootContextTypes.FISHING), Pair.of(ChestLootTableGenerator::new, LootContextTypes.CHEST), Pair.of(EntityLootTableGenerator::new, LootContextTypes.ENTITY), Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK), Pair.of(BarterLootTableGenerator::new, LootContextTypes.BARTER), Pair.of(GiftLootTableGenerator::new, LootContextTypes.GIFT));

	public ModdedLootTableProvider(DataGenerator pGenerator) {
		this.generator = pGenerator;
	}

	/**
	 * Performs this provider's action.
	 */
	public void run(DataCache pCache) {
		Path path = this.generator.getOutput();
		Map<Identifier, LootTable> map = Maps.newHashMap();
		this.getTables().forEach((p_124458_) -> {
			p_124458_.getFirst().get().accept((p_176077_, p_176078_) -> {
				if (map.put(p_176077_, p_176078_.type(p_124458_.getSecond()).build()) != null) {
					throw new IllegalStateException("Duplicate loot table " + p_176077_);
				}
			});
		});
		LootTableReporter validationcontext = new LootTableReporter(LootContextTypes.GENERIC, (p_124465_) -> {
			return null;
		}, map::get);

		validate(map, validationcontext);

		Multimap<String, String> multimap = validationcontext.getMessages();
		if (!multimap.isEmpty()) {
			multimap.forEach((p_124446_, p_124447_) -> {
				LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((p_124451_, p_124452_) -> {
				Path path1 = createPath(path, p_124451_);

				try {
					DataProvider.writeToPath(GSON, pCache, LootManager.toJson(p_124452_), path1);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save loot table {}", path1, ioexception);
				}

			});
		}
	}

	protected List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> getTables() {
		return subProviders;
	}

	protected void validate(Map<Identifier, LootTable> map, LootTableReporter validationtracker) {
		for(Identifier resourcelocation : Sets.difference(LootTables.getAll(), map.keySet())) {
			validationtracker.report("Missing built-in table: " + resourcelocation);
		}

		map.forEach((p_218436_2_, p_218436_3_) -> {
			LootManager.validate(validationtracker, p_218436_2_, p_218436_3_);
		});
	}

	private static Path createPath(Path pPath, Identifier pId) {
		return pPath.resolve("data/" + pId.getNamespace() + "/loot_tables/" + pId.getPath() + ".json");
	}

	/**
	 * Gets a name for this provider, to use in logging.
	 */
	public String getName() {
		return "LootTables";
	}
}
