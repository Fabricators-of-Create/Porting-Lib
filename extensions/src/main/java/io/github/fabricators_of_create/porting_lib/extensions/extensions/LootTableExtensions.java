package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.resources.ResourceLocation;

public interface LootTableExtensions {
	default void setLootTableId(final ResourceLocation id) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default ResourceLocation getLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
