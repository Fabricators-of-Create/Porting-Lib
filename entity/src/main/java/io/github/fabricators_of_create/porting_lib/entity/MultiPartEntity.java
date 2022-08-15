package io.github.fabricators_of_create.porting_lib.entity;

import org.jetbrains.annotations.Nullable;

public interface MultiPartEntity {
	/**
	 * This is used to specify that your entity has multiple individual parts, such as the Vanilla Ender Dragon.
	 *
	 * @return true if this is a multipart entity.
	 */
	default boolean isMultipartEntity()
	{
		return false;
	}

	/**
	 * Gets the individual sub parts that make up this entity.
	 *
	 * The entities returned by this method are NOT saved to the world in nay way, they exist as an extension
	 * of their host entity. The child entity does not track its server-side(or client-side) counterpart, and
	 * the host entity is responsible for moving and managing these children.
	 *
	 * Only used if {@link #isMultipartEntity()} returns true.
	 *
	 * @return The child parts of this entity. The value to be returned here should be cached.
	 */
	@Nullable
	default PartEntity<?>[] getParts()
	{
		return null;
	}
}
