package io.github.fabricators_of_create.porting_lib.extensions.capabilities;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ItemCapabilityExtension extends InitializableCapabilityExtension<ItemStack> {
	@Override
	default void initCapabilities() {}

	@Override
	default ICapabilityProvider initCapabilities(ItemStack type, @Nullable CompoundTag tag) {
		throw new IllegalStateException("should be overridden by mixin");
	}
}
