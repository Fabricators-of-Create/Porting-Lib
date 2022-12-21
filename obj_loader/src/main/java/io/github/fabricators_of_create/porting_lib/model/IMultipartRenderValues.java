package io.github.fabricators_of_create.porting_lib.model;

import org.jetbrains.annotations.Nullable;

/**
 * A standard interface for renderable context values that support providing different values for parts of the model.
 * @param <T> the type of value to be used for each part
 */
public interface IMultipartRenderValues<T> {
	/**
	 * Returns the value for the given part.
	 * @param part the name of the part
	 * @return the context value for the part, or {@code null}
	 */
	@Nullable
	T getPartValues(String part);
}
