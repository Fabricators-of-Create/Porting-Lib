package io.github.fabricators_of_create.porting_lib.util;

import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.jetbrains.annotations.NotNull;

/**
 * This LootItemCondition "forge:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public class CanToolPerformAction implements LootCondition {

	public static final LootConditionType LOOT_CONDITION_TYPE = new LootConditionType(new Serializer());

	final ToolAction action;

	public CanToolPerformAction(ToolAction action) {
		this.action = action;
	}

	public static Builder canToolPerformAction(ToolAction action) {
		return () -> new CanToolPerformAction(action);
	}

	@NotNull
	public LootConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	@NotNull
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemstack = lootContext.get(LootContextParameters.TOOL);
		return itemstack != null && itemstack.canPerformAction(this.action);
	}

	public static class Serializer implements net.minecraft.util.JsonSerializer<CanToolPerformAction> {
		public void serialize(JsonObject json, CanToolPerformAction itemCondition, @NotNull JsonSerializationContext context) {
			json.addProperty("action", itemCondition.action.name());
		}

		@NotNull
		public CanToolPerformAction fromJson(JsonObject json, @NotNull JsonDeserializationContext context) {
			return new CanToolPerformAction(ToolAction.get(json.get("action").getAsString()));
		}
	}

}
