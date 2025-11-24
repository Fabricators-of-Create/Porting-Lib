package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.resources.ResourceLocation;

public interface PortingLibLootTableBuilder {
	default ResourceLocation getPortingLibLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setPortingLibLootTableId(ResourceLocation id) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
