package io.github.fabricators_of_create.porting_lib.common.util;

import java.util.Objects;

/**
 * A predicate that takes three arguments and returns a boolean.
 */
@FunctionalInterface
public interface TriPredicate<T, U, V> {
	boolean test(T t, U u, V v);

	default TriPredicate<T, U, V> and(TriPredicate<? super T, ? super U, ? super V> other) {
		Objects.requireNonNull(other);
		return (T t, U u, V v) -> test(t, u, v) && other.test(t, u, v);
	}

	default TriPredicate<T, U, V> negate() {
		return (T t, U u, V v) -> !test(t, u, v);
	}

	default TriPredicate<T, U, V> or(TriPredicate<? super T, ? super U, ? super V> other) {
		Objects.requireNonNull(other);
		return (T t, U u, V v) -> test(t, u, v) || other.test(t, u, v);
	}
}
