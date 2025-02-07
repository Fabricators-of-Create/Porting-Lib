package io.github.fabricators_of_create.porting_lib.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableIdCondition implements LootCondition {
	public static final LootConditionType LOOT_TABLE_ID = new LootConditionType(new Serializer());
	public static final Identifier UNKNOWN_LOOT_TABLE = PortingLib.id("unknown_loot_table");

	private final Identifier targetLootTableId;

	private LootTableIdCondition(final Identifier targetLootTableId) {
		this.targetLootTableId = targetLootTableId;
	}

	public static io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition.Builder builder(final Identifier targetLootTableId) {
		return new io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition.Builder(targetLootTableId);
	}

	@Override
	public LootConditionType getType() {
		return LOOT_TABLE_ID;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return lootContext.getQueriedLootTableId().equals(this.targetLootTableId);
	}

	public static class Builder implements LootCondition.Builder {
		private final Identifier targetLootTableId;

		public Builder(Identifier targetLootTableId) {
			if (targetLootTableId == null) throw new IllegalArgumentException("Target loot table must not be null");
			this.targetLootTableId = targetLootTableId;
		}

		@Override
		public LootCondition build() {
			return new LootTableIdCondition(this.targetLootTableId);
		}
	}

	public static class Serializer implements net.minecraft.util.JsonSerializer<LootTableIdCondition> {
		@Override
		public void serialize(JsonObject object, LootTableIdCondition instance, JsonSerializationContext ctx) {
			object.addProperty("loot_table_id", instance.targetLootTableId.toString());
		}

		@Override
		public LootTableIdCondition fromJson(JsonObject object, JsonDeserializationContext ctx) {
			return new LootTableIdCondition(new Identifier(JsonHelper.getString(object, "loot_table_id")));
		}
	}
}
