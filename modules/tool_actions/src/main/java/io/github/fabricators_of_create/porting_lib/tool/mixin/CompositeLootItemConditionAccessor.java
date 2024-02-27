package io.github.fabricators_of_create.porting_lib.tool.mixin;

import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeLootItemCondition.class)
public interface CompositeLootItemConditionAccessor {
	@Accessor
	List<LootItemCondition> getTerms();

	@Mutable
	@Accessor
	void setTerms(List<LootItemCondition> terms);
}
