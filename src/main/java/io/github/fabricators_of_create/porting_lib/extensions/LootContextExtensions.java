package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.resources.ResourceLocation;

public interface LootContextExtensions {
	default int getLootingModifier() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
	default ResourceLocation getQueriedLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
