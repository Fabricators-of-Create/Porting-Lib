package io.github.fabricators_of_create.porting_lib.extensions.common;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SlotExtension {
	/**
	 * Sets the background atlas and sprite location.
	 *
	 * @param atlas The atlas name
	 * @param sprite The sprite located on that atlas.
	 * @return this, to allow chaining.
	 */
	default Slot port_lib$setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		throw PortingLib.createMixinException("SlotExtensions.setBackground(ResourceLocation, ResourceLocation)");
	}

	/**
	 * Retrieves the index in the inventory for this slot, this value should typically not
	 * be used, but can be useful for some occasions.
	 *
	 * @return Index in associated inventory for this slot.
	 */
	default int port_lib$getSlotIndex() {
		throw PortingLib.createMixinException("SlotExtensions.getSlotIndex()");
	}

	/**
	 * Checks if the other slot is in the same inventory, by comparing the inventory reference.
	 * @param other
	 * @return true if the other slot is in the same inventory
	 */
	default boolean port_lib$isSameInventory(Slot other) {
		throw PortingLib.createMixinException("SlotExtensions.isSameInventory(Slot)");
	}
}
