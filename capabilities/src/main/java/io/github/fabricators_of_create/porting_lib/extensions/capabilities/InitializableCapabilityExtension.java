package io.github.fabricators_of_create.porting_lib.extensions.capabilities;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import net.minecraft.nbt.CompoundTag;

public interface InitializableCapabilityExtension<T> {
	void initCapabilities();
	default ICapabilityProvider initCapabilities(T type, @Nullable CompoundTag tag) {
		return null;
	}
}
