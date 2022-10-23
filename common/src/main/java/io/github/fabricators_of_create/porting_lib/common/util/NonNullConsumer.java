package io.github.fabricators_of_create.porting_lib.common.util;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface NonNullConsumer<T> extends Consumer<T> {
	default NonNullConsumer<T> andThen(NonNullConsumer<? super T> after) {
		Objects.requireNonNull(after);
		return (T t) -> { accept(t); after.accept(t); };
	}

	static <T> NonNullConsumer<T> noop() {
		return t -> {};
	}
}
