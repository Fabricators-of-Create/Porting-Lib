package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import org.jetbrains.annotations.ApiStatus;

public interface LootPoolExtensions {
	default String getName() {
		throw new RuntimeException();
	}

	@ApiStatus.Internal
	default void setName(String name) {
		throw new RuntimeException();
	}
}
