package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryExtension {
	default void pl$addPackFinder(RepositorySource packFinder) {
		throw new RuntimeException("PackRepository implementation does not support adding sources!");
	}
}
