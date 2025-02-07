package io.github.fabricators_of_create.porting_lib.loot;

import com.google.gson.JsonObject;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.util.Identifier;

/**
 * Abstract base deserializer for LootModifiers. Takes care of Forge registry things.<br/>
 * Modders should extend this class to return their modifier and implement the abstract
 * <code>read</code> method to deserialize from json.
 *
 * @param <T> the Type to deserialize
 */
public abstract class GlobalLootModifierSerializer<T extends IGlobalLootModifier> {
	private final Identifier registryName = null;

	//Helpers

	@SuppressWarnings("unchecked") // Need this wrapper, because generics
	private static <G> Class<G> castClass(Class<?> cls) {
		return (Class<G>) cls;
	}

	/**
	 * Most mods will likely not need more than<br/>
	 * <code>return new MyModifier(conditionsIn)</code><br/>
	 * but any additional properties that are needed will need to be deserialized here.
	 *
	 * @param location        The resource location (if needed)
	 * @param json            The full json object (including ILootConditions)
	 * @param ailootcondition An already deserialized list of ILootConditions
	 */
	public abstract T read(Identifier location, JsonObject json, LootCondition[] ailootcondition);

	/**
	 * Write the serializer to json.
	 * <p>
	 * Most serializers won't have to do anything else than {@link #makeConditions}
	 * Which simply creates the JsonObject from an array of ILootConditions.
	 */
	public abstract JsonObject write(T instance);

	/**
	 * Helper to create the json object from the conditions.
	 * Add any extra properties to the returned json.
	 */
	public JsonObject makeConditions(LootCondition[] conditions) {
		JsonObject json = new JsonObject();
		json.add("conditions", AdvancementEntityPredicateSerializer.INSTANCE.conditionsToJson(conditions));
		return json;
	}
}
