package io.github.fabricators_of_create.porting_lib.common.util;

import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
public interface NonNullSupplier<T> extends Supplier<T> {

	static <T> NonNullSupplier<T> of(Supplier<T> sup) {
		return of(sup, () -> "Unexpected null value from supplier");
	}

	static <T> NonNullSupplier<T> of(Supplier<T> sup, NonNullSupplier<String> errorMsg) {
		return () -> {
			T res = sup.get();
			Objects.requireNonNull(res, errorMsg);
			return res;
		};
	}
}
