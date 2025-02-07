package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Supplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LazyItemGroup extends ItemGroup {

	private final Supplier<ItemStack> stackSupplier;

	public LazyItemGroup(String name, Supplier<ItemStack> stackSupplier) {
		super(ItemGroupUtil.expandArrayAndGetId(), name);
		this.stackSupplier = stackSupplier;
	}

	public LazyItemGroup(String name) {
		this(name, null);
	}

	@Override
	public ItemStack createIcon() {
		return stackSupplier.get();
	}
}
