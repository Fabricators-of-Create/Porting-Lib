package io.github.fabricators_of_create.porting_lib.common.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface NonNullFunction<T, R> extends Function<T, R> {
	default <V> NonNullFunction<T, V> andThen(NonNullFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return t -> after.apply(apply(t));
	}
}
