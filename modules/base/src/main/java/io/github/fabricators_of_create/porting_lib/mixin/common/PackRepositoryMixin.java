package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.PackRepositoryExtension;
import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryExtension {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Override
	public synchronized void port_lib$addPackFinder(RepositorySource packFinder) {
		this.sources.add(packFinder);
	}
}
