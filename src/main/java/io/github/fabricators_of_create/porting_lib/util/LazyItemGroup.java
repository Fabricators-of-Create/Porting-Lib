package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class LazyItemGroup extends CreativeModeTab {

	private final Supplier<ItemStack> stackSupplier;

	public LazyItemGroup(String name, Supplier<ItemStack> stackSupplier) {
		super(ItemGroupUtil.expandArrayAndGetId(), name);
		this.stackSupplier = stackSupplier;
	}

	public LazyItemGroup(String name) {
		this(name, null);
	}

	@Override
	public ItemStack makeIcon() {
		return stackSupplier.get();
	}
}
