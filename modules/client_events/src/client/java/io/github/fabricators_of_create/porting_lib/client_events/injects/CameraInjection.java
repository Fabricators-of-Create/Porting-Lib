package io.github.fabricators_of_create.porting_lib.client_events.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

public interface CameraInjection {
	default float port_lib$getRoll() {
		throw PortingLib.createMixinException("CameraInjection.port_lib$getRoll()");
	}
}
