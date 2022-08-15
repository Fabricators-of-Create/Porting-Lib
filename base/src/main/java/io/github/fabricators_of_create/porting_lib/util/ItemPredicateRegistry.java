package io.github.fabricators_of_create.porting_lib.util;

import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

public class ItemPredicateRegistry {
	public static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> custom_predicates = new java.util.HashMap<>();
	private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);

	public static void register(ResourceLocation name, java.util.function.Function<JsonObject, ItemPredicate> deserializer) {
		custom_predicates.put(name, deserializer);
	}

	public static Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> getPredicates() {
		return unmod_predicates;
	}
}
