package io.github.fabricators_of_create.porting_lib.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.conditions.ConditionalOps;
import io.github.fabricators_of_create.porting_lib.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

public class LootModifierManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static final Logger LOGGER = LogManager.getLogger();

	public static final LootModifierManager INSTANCE = new LootModifierManager();

	private HolderLookup.Provider registries;
	private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
	private static final String folder = "loot_modifiers";
	public static final ResourceLocation ID = PortingLib.id(folder);;

	public LootModifierManager() {
		super(GSON, folder);
	}

	public void injectContext(HolderLookup.Provider registries) {
		this.registries = registries;
	}

	@Override
	protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		Map<ResourceLocation, JsonElement> map = super.prepare(resourceManager, profilerFiller);
		List<ResourceLocation> finalLocations = new ArrayList<>();
		ResourceLocation resourceLocation = PortingLib.neo("loot_modifiers/global_loot_modifiers.json");
		//read in all data files from neoforge:loot_modifiers/global_loot_modifiers in order to do layering
		for (Resource resource : resourceManager.getResourceStack(resourceLocation)) {
			try (Reader reader = resource.openAsReader()) {
				JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
				boolean replace = GsonHelper.getAsBoolean(jsonobject, "replace", false);
				if (replace)
					finalLocations.clear();
				JsonArray entries = GsonHelper.getAsJsonArray(jsonobject, "entries");
				for (int i = 0; i < entries.size(); i++) {
					ResourceLocation loc = ResourceLocation.parse(GsonHelper.convertToString(entries.get(i), "entries[" + i + "]"));
					finalLocations.remove(loc); //remove and re-add if needed, to update the ordering.
					finalLocations.add(loc);
				}
			} catch (RuntimeException | IOException ioexception) {
				LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourceLocation, resource.sourcePackId(), ioexception);
			}
		}
		Map<ResourceLocation, JsonElement> finalMap = new HashMap<>();
		//use layered config to fetch modifier data files (modifiers missing from config are disabled)
		for (ResourceLocation location : finalLocations) {
			finalMap.put(location, map.get(location));
		}
		return finalMap;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
		DynamicOps<JsonElement> ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, registries), ICondition.IContext.EMPTY);
		Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
		for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
			ResourceLocation location = entry.getKey();
			JsonElement json = entry.getValue();
			IGlobalLootModifier.CONDITIONAL_CODEC.parse(ops, json)
					// log error if parse fails
					.resultOrPartial(errorMsg -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, errorMsg))
					// add loot modifier if parse succeeds
					.flatMap(Function.identity())
					.ifPresent(carrier -> builder.put(location, carrier.carrier()));
		}
		this.registeredLootModifiers = builder.build();
	}

	/**
	 * An immutable collection of the registered loot modifiers in layered order.
	 */
	public Collection<IGlobalLootModifier> getAllLootMods() {
		return registeredLootModifiers.values();
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}
}
