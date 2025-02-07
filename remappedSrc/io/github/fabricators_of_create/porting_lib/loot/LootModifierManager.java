package io.github.fabricators_of_create.porting_lib.loot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import io.github.fabricators_of_create.porting_lib.PortingLib;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public class LootModifierManager extends JsonDataLoader implements IdentifiableResourceReloadListener { // FIXME this probably needs to be registered!
	public static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON_INSTANCE = LootGsons.getFunctionGsonBuilder().create();
	private static final String folder = "loot_modifiers";
	public static SimpleRegistry<GlobalLootModifierSerializer> SERIALIZER = FabricRegistryBuilder.createSimple(GlobalLootModifierSerializer.class, PortingLib.id("loot_modifier")).buildAndRegister();
	private Map<Identifier, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();

	public LootModifierManager() {
		super(GSON_INSTANCE, folder);
	}

	public static GlobalLootModifierSerializer<?> getSerializerForName(Identifier resourcelocation) {
		return SERIALIZER.get(resourcelocation);
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> resourceList, ResourceManager resourceManagerIn, Profiler profilerIn) {
		ImmutableMap.Builder<Identifier, IGlobalLootModifier> builder = ImmutableMap.builder();
		//old way (for reference)
        /*Map<IGlobalLootModifier, ResourceLocation> toLocation = new HashMap<IGlobalLootModifier, ResourceLocation>();
        resourceList.forEach((location, object) -> {
            try {
                IGlobalLootModifier modifier = deserializeModifier(location, object);
                builder.put(location, modifier);
                toLocation.put(modifier, location);
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse loot modifier {}", location, exception);
            }
        });
        builder.orderEntriesByValue((x,y) -> {
            return toLocation.get(x).compareTo(toLocation.get(y));
        });*/
		//new way
		ArrayList<Identifier> finalLocations = new ArrayList<Identifier>();
		Identifier resourcelocation = new Identifier("forge", "loot_modifiers/global_loot_modifiers.json");
		try {
			//read in all data files from forge:loot_modifiers/global_loot_modifiers in order to do layering
			for (Resource iresource : resourceManagerIn.getAllResources(resourcelocation)) {
				try (InputStream inputstream = iresource.getInputStream();
					 Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
				) {
					JsonObject jsonobject = JsonHelper.deserialize(GSON_INSTANCE, reader, JsonObject.class);
					boolean replace = jsonobject.get("replace").getAsBoolean();
					if (replace) finalLocations.clear();
					JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
					for (JsonElement entry : entryList) {
						String loc = entry.getAsString();
						Identifier res = new Identifier(loc);
						finalLocations.remove(res);
						finalLocations.add(res);
					}
				} catch (RuntimeException | IOException ioexception) {
					LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.getResourcePackName(), ioexception);
				} finally {
					IOUtils.closeQuietly(iresource);
				}
			}
		} catch (IOException ioexception1) {
			LOGGER.error("Couldn't read global loot modifier list from {}", resourcelocation, ioexception1);
		}
		//use layered config to fetch modifier data files (modifiers missing from config are disabled)
		finalLocations.forEach(location -> {
			try {
				IGlobalLootModifier modifier = deserializeModifier(location, resourceList.get(location));
				if (modifier != null)
					builder.put(location, modifier);
			} catch (Exception exception) {
				LOGGER.error("Couldn't parse loot modifier {}", location, exception);
			}
		});
		ImmutableMap<Identifier, IGlobalLootModifier> immutablemap = builder.build();
		this.registeredLootModifiers = immutablemap;
	}

	private IGlobalLootModifier deserializeModifier(Identifier location, JsonElement element) {
		if (!element.isJsonObject()) return null;
		JsonObject object = element.getAsJsonObject();
		LootCondition[] lootConditions = GSON_INSTANCE.fromJson(object.get("conditions"), LootCondition[].class);

		// For backward compatibility with the initial implementation, fall back to using the location as the type.
		// TODO: Remove fallback in 1.16
		Identifier serializer = location;
		if (object.has("type")) {
			serializer = new Identifier(JsonHelper.getString(object, "type"));
		}

		return SERIALIZER.get(serializer).read(location, object, lootConditions);
	}

	/**
	 * An immutable collection of the registered loot modifiers in layered order.
	 *
	 * @return
	 */
	public Collection<IGlobalLootModifier> getAllLootMods() {
		return registeredLootModifiers.values();
	}

	private static LootModifierManager INSTANCE;

	public static void init() {
		INSTANCE = new LootModifierManager();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(INSTANCE);
	}

	public static LootModifierManager getLootModifierManager() {
		if(INSTANCE == null)
			throw new IllegalStateException("Can not retrieve LootModifierManager until resources have loaded once.");
		return INSTANCE;
	}

	@Override
	public Identifier getFabricId() {
		return PortingLib.id("loot_modifier_manager");
	}
}
