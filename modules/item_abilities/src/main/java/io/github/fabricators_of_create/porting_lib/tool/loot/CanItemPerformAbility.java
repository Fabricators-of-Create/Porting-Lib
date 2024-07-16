package io.github.fabricators_of_create.porting_lib.tool.loot;

import com.google.common.collect.ImmutableSet;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This LootItemCondition "porting_lib:can_item_perform_ability" can be used to check if a tool can perform a given ItemAbility.
 */
public class CanItemPerformAbility implements LootItemCondition {
	public static MapCodec<CanItemPerformAbility> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
					.group(
							ItemAbility.CODEC.fieldOf("ability").forGetter(action -> action.ability))
					.apply(builder, CanItemPerformAbility::new));

	public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(CODEC);

	final ItemAbility ability;

	public CanItemPerformAbility(ItemAbility ability) {
		this.ability = ability;
	}

	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemstack = lootContext.getParamOrNull(LootContextParams.TOOL);
		return itemstack != null && itemstack.canPerformAction(this.ability);
	}

	public static LootItemCondition.Builder canItemPerformAbility(ItemAbility action) {
		return () -> new CanItemPerformAbility(action);
	}

	public static void init() {
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, PortingLib.id("can_item_perform_ability"), CanItemPerformAbility.LOOT_CONDITION_TYPE);
	}
}

