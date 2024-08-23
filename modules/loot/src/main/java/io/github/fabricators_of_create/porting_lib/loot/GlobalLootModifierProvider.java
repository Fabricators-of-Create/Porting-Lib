package io.github.fabricators_of_create.porting_lib.loot;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.core.util.LamdbaExceptionUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Provider for forge's GlobalLootModifier system. See {@link LootModifier}
 *
 * This provider only requires implementing {@link #start()} and calling {@link #add} from it.
 */
public abstract class GlobalLootModifierProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final PackOutput output;
	private final String modid;
	private final Map<String, JsonElement> toSerialize = new HashMap<>();
	private boolean replace = false;

	public GlobalLootModifierProvider(PackOutput output, String modid) {
		this.output = output;
		this.modid = modid;
	}

	/**
	 * Sets the "replace" key in global_loot_modifiers to true.
	 */
	protected void replacing() {
		this.replace = true;
	}

	/**
	 * Call {@link #add} here, which will pass in the necessary information to write the jsons.
	 */
	protected abstract void start();

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		start();

		Path forgePath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve("neoforge").resolve("loot_modifiers").resolve("global_loot_modifiers.json");
		Path modifierFolderPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("loot_modifiers");
		List<ResourceLocation> entries = new ArrayList<>();

		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, json) -> {
			entries.add(ResourceLocation.fromNamespaceAndPath(modid, name));
			Path modifierPath = modifierFolderPath.resolve(name + ".json");
			futuresBuilder.add(DataProvider.saveStable(cache, json, modifierPath));
		}));

		JsonObject forgeJson = new JsonObject();
		forgeJson.addProperty("replace", this.replace);
		forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));

		futuresBuilder.add(DataProvider.saveStable(cache, forgeJson, forgePath));

		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}

	/**
	 * Passes in the data needed to create the file without any extra objects.
	 *
	 * @param modifier      The name of the modifier, which will be the file name.
	 * @param instance      The instance to serialize
	 */
	public <T extends IGlobalLootModifier> void add(String modifier, T instance) {
		JsonElement json = IGlobalLootModifier.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(s -> new IllegalStateException("Could not encode global loot modifier. " + s));
		this.toSerialize.put(modifier, json);
	}

	@Override
	public String getName() {
		return "Global Loot Modifiers : " + modid;
	}
}
