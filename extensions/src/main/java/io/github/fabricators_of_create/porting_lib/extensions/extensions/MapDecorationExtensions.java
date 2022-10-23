package io.github.fabricators_of_create.porting_lib.extensions.extensions;

public interface MapDecorationExtensions {
	/**
	 * Renders this decoration, useful for custom sprite sheets.
	 * @param index The index of this icon in the MapData's list. Used by vanilla to offset the Z-coordinate to prevent Z-fighting
	 * @return false to run vanilla logic for this decoration, true to skip it
	 */
	default boolean render(int index) {
		return false;
	}
}
