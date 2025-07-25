package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.injects.ItemContainerContentsInjection;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemContainerContents.class)
public abstract class ItemContainerContentsMixin implements ItemContainerContentsInjection {
	@Shadow
	@Final
	private NonNullList<ItemStack> items;

	@Override
	public int port_lib$getSlots() {
		return items.size();
	}

	@Override
	public ItemStack port_lib$getStackInSlot(int slot) {
		port_lib$validateSlotIndex(slot);
		return items.get(slot).copy();
	}

	@Unique
	private void port_lib$validateSlotIndex(int slot) {
		if (slot < 0 || slot >= port_lib$getSlots()) {
			throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + port_lib$getSlots() + ")");
		}
	}
}
