package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

@Mixin(BlockLootTableGenerator.class)
public interface BlockLootAccessor {
	@Accessor
	Map<Identifier, LootTable.Builder> getMap();
}
