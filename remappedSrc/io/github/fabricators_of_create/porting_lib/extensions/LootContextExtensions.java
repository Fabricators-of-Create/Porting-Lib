package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.util.Identifier;

public interface LootContextExtensions {
	default int getLootingModifier() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setQueriedLootTableId(Identifier queriedLootTableId) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
	default Identifier getQueriedLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
