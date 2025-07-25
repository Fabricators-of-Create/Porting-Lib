package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.injections.PackRepositoryInjection;
import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin implements PackRepositoryInjection {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Override
	public synchronized void port_lib$addPackFinder(RepositorySource packFinder) {
		this.sources.add(packFinder);
	}
}
