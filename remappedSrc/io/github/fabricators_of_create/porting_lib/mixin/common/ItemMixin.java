package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.ItemExtensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;

@Mixin(Item.class)
public abstract class ItemMixin implements RegistryNameProvider, ItemExtensions {

	@Unique
	private Identifier port_lib$registryName = null;

	@Override
	public Identifier getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.ITEM.getId((Item) (Object) this);
		}
		return port_lib$registryName;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return false;
	}
}
