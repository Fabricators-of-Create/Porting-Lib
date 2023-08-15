package io.github.fabricators_of_create.porting_lib.util;

import java.util.Map;

import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;

public class ItemPredicateRegistry {
	public static final Map<ResourceLocation, Codec<ItemPredicate>> custom_predicates = new java.util.HashMap<>();
	private static final Map<ResourceLocation, Codec<ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);

	public static void register(ResourceLocation name, Codec<ItemPredicate> deserializer) {
		custom_predicates.put(name, deserializer);
	}

	public static Map<ResourceLocation, Codec<ItemPredicate>> getPredicates() {
		return unmod_predicates;
	}
}
