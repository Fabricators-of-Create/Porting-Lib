package io.github.fabricators_of_create.porting_lib.util;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

public class ItemPredicateRegistry {
	public static final Map<Identifier, Function<JsonObject, ItemPredicate>> custom_predicates = new java.util.HashMap<>();
	private static final Map<Identifier, java.util.function.Function<JsonObject, ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);

	public static void register(Identifier name, java.util.function.Function<JsonObject, ItemPredicate> deserializer) {
		custom_predicates.put(name, deserializer);
	}

	public static Map<Identifier, java.util.function.Function<JsonObject, ItemPredicate>> getPredicates() {
		return unmod_predicates;
	}
}
