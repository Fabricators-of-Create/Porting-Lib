package io.github.fabricators_of_create.porting_lib.render;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapDecorationIterator implements Iterator<MapDecoration> {

	private final Iterator<? extends MapDecoration> wrapped;
	private final AtomicInteger index;

	public MapDecoration nextCached = null;

	public MapDecorationIterator(Iterator<? extends MapDecoration> iterator, AtomicInteger index) {
		this.wrapped = iterator;
		this.index = index;
	}

	@Override
	public boolean hasNext() {
		if (wrapped.hasNext()) {
			MapDecoration value = wrapped.next();

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
	public MapDecoration next() {
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
