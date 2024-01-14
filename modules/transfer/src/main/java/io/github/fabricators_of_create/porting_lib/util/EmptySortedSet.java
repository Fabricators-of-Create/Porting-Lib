package io.github.fabricators_of_create.porting_lib.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EmptySortedSet implements SortedSet<Object> {
	INSTANCE;

	public static <T> SortedSet<T> cast() {
		//noinspection unchecked
		return (SortedSet<T>) INSTANCE;
	}

	@Nullable
	@Override
	public Comparator<? super Object> comparator() {
		return null;
	}

	@NotNull
	@Override
	public SortedSet<Object> subSet(Object fromElement, Object toElement) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public SortedSet<Object> headSet(Object toElement) {
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public SortedSet<Object> tailSet(Object fromElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object first() {
		throw new NoSuchElementException();
	}

	@Override
	public Object last() {
		throw new NoSuchElementException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@NotNull
	@Override
	public Iterator<Object> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		return a;
	}

	@Override
	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
}
