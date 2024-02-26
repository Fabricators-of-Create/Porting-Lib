package io.github.fabricators_of_create.porting_lib.extensions.extensions;

public interface GrindstoneMenuExtension {
	default int getXp() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
