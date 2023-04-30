package io.github.fabricators_of_create.porting_lib.entity;

/**
 * An entity that has multiple sub-entities that make up parts of it, like the Ender Dragon.
 */
public interface MultiPartEntity {
	/**
	 * @return a cached array of this entity's parts
	 * @see PartEntity
	 */
	PartEntity<?>[] getParts();

	default boolean hasParts() {
		return getParts().length != 0;
	}
}
