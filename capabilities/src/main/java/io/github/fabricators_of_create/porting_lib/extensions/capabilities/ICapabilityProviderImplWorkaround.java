package io.github.fabricators_of_create.porting_lib.extensions.capabilities;

import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityProviderWorkaround;
import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProviderImpl;

public interface ICapabilityProviderImplWorkaround<T extends ICapabilityProviderImpl<T>> extends ICapabilityProviderImpl<T> {
	default CapabilityProviderWorkaround<T> port_lib$getWorkaround() {
		throw new IllegalStateException("should be overridden by mixin");
	}
}
