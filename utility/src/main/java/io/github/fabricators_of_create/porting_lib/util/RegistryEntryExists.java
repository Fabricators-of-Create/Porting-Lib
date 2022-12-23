package io.github.fabricators_of_create.porting_lib.util;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class RegistryEntryExists implements ConditionJsonProvider {
	public static final ResourceLocation ID = PortingConstants.id("registry_entry_exists");

	private final ResourceKey<? extends Registry<?>> registry;
	private final ResourceLocation item;

	public static void init() {
		ResourceConditions.register(ID, jsonObject -> {
			ResourceLocation registry = new ResourceLocation(GsonHelper.getAsString(jsonObject, "registry"));
			ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(jsonObject, "id"));
			return BuiltInRegistries.REGISTRY.get(registry).getOptional(id).isPresent();
		});
	}

	public RegistryEntryExists(ResourceKey<? extends Registry<?>> registry, String location) {
		this(registry, new ResourceLocation(location));
	}

	public RegistryEntryExists(ResourceKey<? extends Registry<?>> registry, String namespace, String path) {
		this(registry, new ResourceLocation(namespace, path));
	}

	public RegistryEntryExists(ResourceKey<? extends Registry<?>> registry, ResourceLocation item) {
		this.registry = registry;
		this.item = item;
	}

	@Override
	public ResourceLocation getConditionId() {
		return ID;
	}

	@Override
	public void writeParameters(JsonObject object) {
		object.addProperty("registry", registry.location().toString());
		object.addProperty("id", item.toString());
	}
}
