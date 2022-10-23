package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.level.storage.loot.LootPool;

public interface LootPoolBuilderExtension {
	default LootPool.Builder name(String name) {
		throw new RuntimeException();
	}
}
