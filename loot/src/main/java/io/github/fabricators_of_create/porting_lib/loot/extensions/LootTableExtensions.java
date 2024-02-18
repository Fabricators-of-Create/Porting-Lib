package io.github.fabricators_of_create.porting_lib.loot.extensions;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

public interface LootTableExtensions {
	// hack to access a field defined in one mixin in another.
	@ApiStatus.Internal
	default ThreadLocal<LootCollector> port_lib$lootCollector() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setLootTableId(final ResourceLocation id) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default ResourceLocation getLootTableId() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
