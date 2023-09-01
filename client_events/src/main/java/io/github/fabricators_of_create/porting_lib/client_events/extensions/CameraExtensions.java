package io.github.fabricators_of_create.porting_lib.client_events.extensions;

public interface CameraExtensions {
	default void setAnglesInternal(float yaw, float pitch) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
