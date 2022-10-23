package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class LazyItemGroup extends FabricItemGroup {

	private final Supplier<ItemStack> stackSupplier;
	private final Component name;

	public LazyItemGroup(ResourceLocation id, Component name, Supplier<ItemStack> stackSupplier) {
		super(id);
		this.name = name;
		this.stackSupplier = stackSupplier;
	}

	public LazyItemGroup(ResourceLocation id, Component name) {
		this(id, name, null);
	}

	@Override
	public Component getDisplayName() {
		return name;
	}

	@Override
	public ItemStack makeIcon() {
		return stackSupplier.get();
	}
}
