package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryInjection {
	default void port_lib$addPackFinder(RepositorySource packFinder) {
		throw PortingLib.createMixinException("PackRepositoryExtension.addPackFinder(RepositorySource)");
	}
}
