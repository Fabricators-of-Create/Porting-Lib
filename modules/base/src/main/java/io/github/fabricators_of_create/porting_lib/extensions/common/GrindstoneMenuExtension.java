package io.github.fabricators_of_create.porting_lib.extensions.common;

public interface GrindstoneMenuExtension {
	default int port_lib$getXp() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
