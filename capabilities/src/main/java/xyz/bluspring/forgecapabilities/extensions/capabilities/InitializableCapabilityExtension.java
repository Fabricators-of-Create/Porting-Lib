package xyz.bluspring.forgecapabilities.extensions.capabilities;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;

public interface InitializableCapabilityExtension<T> {
	void initCapabilities();
	default ICapabilityProvider initCapabilities(T type, @Nullable CompoundTag tag) {
		return null;
	}
}
