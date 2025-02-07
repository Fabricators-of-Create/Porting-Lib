package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.util.Identifier;

public interface LootTableExtensions {
	default void setLootTableId(final Identifier id) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Identifier getLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
