package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public abstract class LazyItemGroup extends CreativeModeTab {

	private final Supplier<ItemStack> stackSupplier;

	public LazyItemGroup(Component name, Supplier<ItemStack> stackSupplier) {
		super(ItemGroupUtil.expandArrayAndGetId(), name);
		this.stackSupplier = stackSupplier;
	}

	public LazyItemGroup(Component name) {
		this(name, null);
	}

	@Override
	public ItemStack makeIcon() {
		return stackSupplier.get();
	}
}
