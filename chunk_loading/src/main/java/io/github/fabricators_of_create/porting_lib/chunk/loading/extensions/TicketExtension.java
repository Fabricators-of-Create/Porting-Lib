package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import org.jetbrains.annotations.ApiStatus;

public interface TicketExtension {
	@ApiStatus.Internal
	default void setForceTicks(boolean forceTicks) {
		throw new RuntimeException();
	}

	default boolean isForceTicks() {
		throw new RuntimeException();
	}
}
