package xyz.bluspring.forgecapabilities.capabilities;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;

// Due to a mixin issue where you can't exactly extend new classes in it, this is a
// little workaround to help redirect method calls to CapabilityProvider.
@ApiStatus.Internal
public class CapabilityProviderWorkaround<B extends ICapabilityProviderImpl<B>> extends CapabilityProvider<B> {
    private final B base;

    public CapabilityProviderWorkaround(Class<B> baseClass, B base) {
        this(baseClass, false, base);
    }

    public CapabilityProviderWorkaround(Class<B> baseClass, boolean isLazy, B base) {
        super(baseClass);
        this.base = base;
    }

    public void invokeGatherCapabilities() {
        this.gatherCapabilities();
    }

    public void invokeGatherCapabilities(ICapabilityProvider parent) {
        this.gatherCapabilities(parent);
    }

    public void invokeGatherCapabilities(Supplier<ICapabilityProvider> parent) {
        this.gatherCapabilities(parent);
    }

    public CapabilityDispatcher invokeGetCapabilities() {
        return this.getCapabilities();
    }

    public CompoundTag invokeSerializeCaps() {
        return this.serializeCaps();
    }

    public void invokeDeserializeCaps(CompoundTag tag) {
        this.deserializeCaps(tag);
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull B getProvider() {
        return base;
    }
}
