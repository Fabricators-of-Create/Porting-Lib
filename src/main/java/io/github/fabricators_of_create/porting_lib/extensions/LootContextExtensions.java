package io.github.fabricators_of_create.porting_lib.extensions;

public interface LootContextExtensions {
	default int getLootingModifier() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
