package io.github.fabricators_of_create.porting_lib.tool.loot;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * This LootItemCondition "porting_lib:can_tool_perform_action" can be used to check if a tool can perform a given ToolAction.
 */
public class CanToolPerformAction implements LootItemCondition {
	public static final Codec<CanToolPerformAction> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("action").forGetter(canToolPerformAction -> canToolPerformAction.action.name())
			).apply(instance, action -> new CanToolPerformAction(ToolAction.get(action))));

	public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(CODEC);

	final ToolAction action;

	public CanToolPerformAction(ToolAction action) {
		this.action = action;
	}

	@NotNull
	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	@NotNull
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemstack = lootContext.getParamOrNull(LootContextParams.TOOL);
		return itemstack != null && itemstack.canPerformAction(this.action);
	}

	public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
		return () -> new CanToolPerformAction(action);
	}

	public static void init() {
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, PortingLib.id("can_tool_perform_action"), CanToolPerformAction.LOOT_CONDITION_TYPE);
	}
}
