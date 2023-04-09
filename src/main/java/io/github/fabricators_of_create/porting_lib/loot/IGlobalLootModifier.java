package io.github.fabricators_of_create.porting_lib.loot;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import org.jetbrains.annotations.NotNull;

public interface IGlobalLootModifier {
	@NotNull
	List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context);
}
