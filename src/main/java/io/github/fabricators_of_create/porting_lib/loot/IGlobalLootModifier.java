package io.github.fabricators_of_create.porting_lib.loot;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public interface IGlobalLootModifier {
	@Nonnull
	List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context);
}
