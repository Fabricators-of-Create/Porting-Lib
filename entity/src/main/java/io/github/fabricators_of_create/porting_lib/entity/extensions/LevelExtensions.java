package io.github.fabricators_of_create.porting_lib.entity.extensions;

import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

public interface LevelExtensions {
	/**
	 * All part entities in this world. Used when collecting entities in an AABB to fix parts being
	 * ignored whose parent entity is in a chunk that does not intersect with the AABB.
	 */
	default Collection<PartEntity<?>> getPartEntities() {
		return getPartEntityMap().values();
	}

	default Int2ObjectMap<PartEntity<?>> getPartEntityMap() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
