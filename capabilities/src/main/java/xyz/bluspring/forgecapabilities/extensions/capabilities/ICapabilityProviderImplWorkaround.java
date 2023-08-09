package xyz.bluspring.forgecapabilities.extensions.capabilities;

import xyz.bluspring.forgecapabilities.capabilities.CapabilityProviderWorkaround;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProviderImpl;

public interface ICapabilityProviderImplWorkaround<T extends ICapabilityProviderImpl<T>> extends ICapabilityProviderImpl<T> {
	default CapabilityProviderWorkaround<T> port_lib$getWorkaround() {
		throw new IllegalStateException("should be overridden by mixin");
	}
}
