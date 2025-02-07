package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {
	@Accessor("slot")
	int port_lib$getSlotIndex();
}
