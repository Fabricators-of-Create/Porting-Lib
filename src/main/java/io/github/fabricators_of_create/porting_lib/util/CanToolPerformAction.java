package io.github.fabricators_of_create.porting_lib.util;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * This LootItemCondition "forge:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public class CanToolPerformAction implements LootItemCondition {

	public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(new Serializer());

	final ToolAction action;

	public CanToolPerformAction(ToolAction action) {
		this.action = action;
	}

	public static Builder canToolPerformAction(ToolAction action) {
		return () -> new CanToolPerformAction(action);
	}

	@Nonnull
	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	@Nonnull
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemstack = lootContext.getParamOrNull(LootContextParams.TOOL);
		return itemstack != null && itemstack.canPerformAction(this.action);
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<CanToolPerformAction> {
		public void serialize(JsonObject json, CanToolPerformAction itemCondition, @Nonnull JsonSerializationContext context) {
			json.addProperty("action", itemCondition.action.name());
		}

		@Nonnull
		public CanToolPerformAction deserialize(JsonObject json, @Nonnull JsonDeserializationContext context) {
			return new CanToolPerformAction(ToolAction.get(json.get("action").getAsString()));
		}
	}

}
