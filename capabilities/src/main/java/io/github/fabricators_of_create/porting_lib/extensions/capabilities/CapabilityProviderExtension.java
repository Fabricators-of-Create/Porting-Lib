package io.github.fabricators_of_create.porting_lib.extensions.capabilities;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityDispatcher;
import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import net.minecraft.nbt.CompoundTag;

// Due to a mixin issue where you can't extend abstract classes inside a mixin, this is a
// little helper to help redirect method calls to CapabilityProvider, while also
// allowing the ability to override the methods. Very handy.
public interface CapabilityProviderExtension {
    default void gatherCapabilities() {
        gatherCapabilities(() -> null);
    }

    default void gatherCapabilities(@Nullable ICapabilityProvider parent) {
        gatherCapabilities(() -> parent);
    }

    default void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
        throw new IllegalStateException();
    }

    @Nullable
    default CapabilityDispatcher getCapabilities() {
        throw new IllegalStateException();
    }

    @Nullable
    default CompoundTag serializeCaps() {
        throw new IllegalStateException();
    }

    default void deserializeCaps(CompoundTag tag) {
        throw new IllegalStateException();
    }
}
