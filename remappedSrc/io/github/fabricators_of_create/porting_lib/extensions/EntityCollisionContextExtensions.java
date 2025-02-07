package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.entity.Entity;

public interface EntityCollisionContextExtensions {
	default Entity getCachedEntity() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
