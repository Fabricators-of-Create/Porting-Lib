package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SlotExtensions {
	default Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default int getSlotIndex() {
		return 0;
	}
}
