package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockLootSubProvider.class)
public interface BlockLootSubProviderAccessor {
	@Accessor
	Map<ResourceLocation, LootTable.Builder> getMap();
}
