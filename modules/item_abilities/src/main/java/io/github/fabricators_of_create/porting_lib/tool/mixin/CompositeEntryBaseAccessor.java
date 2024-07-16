package io.github.fabricators_of_create.porting_lib.tool.mixin;

import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeEntryBase.class)
public interface CompositeEntryBaseAccessor {
	@Accessor
	List<LootPoolEntryContainer> getChildren();
}
