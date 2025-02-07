package io.github.fabricators_of_create.porting_lib.render;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.item.map.MapIcon;

public class MapDecorationIterator implements Iterator<MapIcon> {

	private final Iterator<? extends MapIcon> wrapped;
	private final AtomicInteger index;

	public MapIcon nextCached = null;

	public MapDecorationIterator(Iterator<? extends MapIcon> iterator, AtomicInteger index) {
		this.wrapped = iterator;
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		if (wrapped.hasNext()) {
			MapIcon value = wrapped.next();

			while (value.render(index.get())) {
				if (!this.wrapped.hasNext()) {
					value = null;

					break;
				}

				this.index.incrementAndGet();

				value = wrapped.next();
			}

			this.nextCached = value;

			return value != null;
		}

		return false;
	}

	@Override
	public MapIcon next() {
		if (this.nextCached != null) {
			return this.nextCached;
		} else if (this.wrapped.hasNext()) {
			return wrapped.next();
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		wrapped.remove();
	}
}
