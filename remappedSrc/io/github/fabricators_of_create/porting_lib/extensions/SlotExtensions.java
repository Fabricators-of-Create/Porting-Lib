package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public interface SlotExtensions {
	default Slot setBackground(Identifier atlas, Identifier sprite) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default int getSlotIndex() {
		return 0;
	}
}
