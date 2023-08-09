package xyz.bluspring.forgecapabilities.capabilities;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal // Modders should use ICapabilityProvider, this is for Forge
public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>> extends ICapabilityProvider
{
    boolean areCapsCompatible(CapabilityProvider<B> other);
    boolean areCapsCompatible(@Nullable CapabilityDispatcher other);
    void invalidateCaps();
    void reviveCaps();
}
