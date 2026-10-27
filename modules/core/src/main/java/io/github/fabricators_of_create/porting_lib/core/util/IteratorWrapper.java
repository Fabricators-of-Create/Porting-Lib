package io.github.fabricators_of_create.porting_lib.core.util;

import java.util.Iterator;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

public class IteratorWrapper<T> implements Iterator<T> {
	private final Iterator<T> original;
	private final Function<T, @Nullable T> modifier;

	private @Nullable T nextCached;

	public IteratorWrapper(Iterator<T> original, Function<T, @Nullable T> modifier) {
		this.original = original;
		this.modifier = modifier;
	}

	@Override
	public boolean hasNext() {
		if (original.hasNext()) {
			T value = original.next();
			nextCached = modifier.apply(value);

			if (nextCached == null)
				return original.hasNext();
			else
				return true;
		}

		return false;
	}

	@Override
	public T next() {
		if (nextCached != null) {
			T value = nextCached;
			nextCached = null;
			return value;
		}

		return original.next();
	}

	@Override
	public void remove() {
		original.remove();
	}
}
