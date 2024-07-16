package io.github.fabricators_of_create.porting_lib.tool.mixin;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPoolEntryContainer.class)
public interface LootPoolEntryContainerAccessor {
	@Accessor
	List<LootItemCondition> getConditions();
}
