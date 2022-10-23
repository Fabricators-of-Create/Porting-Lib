package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.entity.Entity;

public interface EntityCollisionContextExtensions {
	default Entity getCachedEntity() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
