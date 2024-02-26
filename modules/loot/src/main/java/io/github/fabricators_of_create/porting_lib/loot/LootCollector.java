package io.github.fabricators_of_create.porting_lib.loot;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootCollector implements Consumer<ItemStack> {
	private final Consumer<ItemStack> wrapped;
	private final ObjectArrayList<ItemStack> stacks = new ObjectArrayList<>();

	public LootCollector(Consumer<ItemStack> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void accept(ItemStack stack) {
		this.stacks.add(stack);
	}

	public void finish(ResourceLocation tableId, LootContext ctx) {
		PortingLibLoot.modifyLoot(tableId, stacks, ctx).forEach(wrapped);
		stacks.clear();
	}
}
