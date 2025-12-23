package io.github.fabricators_of_create.porting_lib.core.util;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface MutableDataComponentHolder extends DataComponentHolder {
	/**
	 * Sets a data component.
	 */
	@Nullable
	default <T> T set(DataComponentType<? super T> componentType, @Nullable T value) {
		return this.method_57379(componentType, value);
	}

	/**
	 * Just like {@link MutableDataComponentHolder#set(Supplier, Object)}, except this runs in production environments.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.NonExtendable
	@Nullable
	default <T> T method_57379(DataComponentType<? super T> componentType, @Nullable T value) {
		return this.set(componentType, value);
	}

	/**
	 * Sets a data component.
	 */
	@Nullable
	default <T> T set(Supplier<? extends DataComponentType<? super T>> componentType, @Nullable T value) {
		return set(componentType.get(), value);
	}

	/**
	 * Updates a data component if it exists, using an additional {@code updateContext}.
	 */
	@Nullable
	default <T, U> T update(DataComponentType<T> componentType, T value, U updateContext, BiFunction<T, U, T> updater) {
		return set(componentType, updater.apply(getOrDefault(componentType, value), updateContext));
	}

	/**
	 * Updates a data component if it exists, using an additional {@code updateContext}.
	 */
	@Nullable
	default <T, U> T update(Supplier<? extends DataComponentType<T>> componentType, T value, U updateContext, BiFunction<T, U, T> updater) {
		return update(componentType.get(), value, updateContext, updater);
	}

	/**
	 * Updates a data component if it exists.
	 */
	@Nullable
	default <T> T update(DataComponentType<T> componentType, T value, UnaryOperator<T> updater) {
		return set(componentType, updater.apply(getOrDefault(componentType, value)));
	}

	/**
	 * Updates a data component if it exists.
	 */
	@Nullable
	default <T> T update(Supplier<? extends DataComponentType<T>> componentType, T value, UnaryOperator<T> updater) {
		return update(componentType.get(), value, updater);
	}

	/**
	 * Removes a data component.
	 */
	@Nullable
	default <T> T remove(DataComponentType<? extends T> componentType) {
		return this.method_57381(componentType);
	}

	/**
	 * Just like {@link MutableDataComponentHolder#remove(DataComponentType)}, except this runs in production environments.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.NonExtendable
	@Nullable
	default <T> T method_57381(DataComponentType<? extends T> componentType) {
		return this.remove(componentType);
	}

	/**
	 * Removes a data component.
	 */
	@Nullable
	default <T> T remove(Supplier<? extends DataComponentType<? extends T>> componentType) {
		return remove(componentType.get());
	}

	/**
	 * Copies all data components from {@code src}
	 *
	 * @implNote This will clear any components if the requested {@code src} holder does not contain a matching value.
	 */
	default void copyFrom(DataComponentHolder src, DataComponentType<?>... componentTypes) {
		for (var componentType : componentTypes) {
			copyFrom(componentType, src);
		}
	}

	/**
	 * Copies all data components from {@code src}
	 *
	 * @implNote This will clear any components if the requested {@code src} holder does not contain a matching value.
	 */
	default void copyFrom(DataComponentHolder src, Supplier<? extends DataComponentType<?>>... componentTypes) {
		for (var componentType : componentTypes) {
			copyFrom(componentType.get(), src);
		}
	}

	/**
	 * Applies a set of component changes to this stack.
	 */
	default void applyComponents(DataComponentPatch patch) {
		this.method_57366(patch);
	}

	/**
	 * Just like {@link MutableDataComponentHolder#applyComponents(DataComponentPatch)}, except this runs in production environments.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.NonExtendable
	default void method_57366(DataComponentPatch patch) {
		this.applyComponents(patch);
	}

	/**
	 * Applies a set of component changes to this stack.
	 */
	default void applyComponents(DataComponentMap components) {
		this.method_57365(components);
	}

	/**
	 * Just like {@link MutableDataComponentHolder#applyComponents(DataComponentMap)}, except this runs in production environments.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.NonExtendable
	default void method_57365(DataComponentMap patch) {
		this.applyComponents(patch);
	}

	private <T> void copyFrom(DataComponentType<T> componentType, DataComponentHolder src) {
		set(componentType, src.get(componentType));
	}
}
