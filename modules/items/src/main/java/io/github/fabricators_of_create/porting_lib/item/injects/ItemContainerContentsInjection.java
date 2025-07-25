package io.github.fabricators_of_create.porting_lib.item.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.item.ItemStack;

public interface ItemContainerContentsInjection {
	/**
	 * @return the number of slots in this container
	 */
	default int port_lib$getSlots() {
		throw PortingLib.createMixinException("ItemContainerContentsInjection.getSlots");
	}

	/**
	 * Gets a copy of the stack at a particular slot.
	 * @param slot The slot to check. Must be within [0, {@link #port_lib$getSlots()}]
	 * @return A copy of the stack in that slot
	 * @throws UnsupportedOperationException if the provided slot index is out-of-bounds.
	 */
	default ItemStack port_lib$getStackInSlot(int slot) {
		throw PortingLib.createMixinException("ItemContainerContentsInjection.getStackInSlot");
	}
}
