package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public interface SlotExtensions {
	Slot setBackground(ResourceLocation atlas, ResourceLocation sprite);
}
