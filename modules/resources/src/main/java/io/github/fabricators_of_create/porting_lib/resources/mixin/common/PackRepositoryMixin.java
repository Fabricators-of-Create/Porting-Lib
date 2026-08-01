package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.ImmutableSet;

import io.github.fabricators_of_create.porting_lib.resources.injections.PackRepositoryInjection;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin implements PackRepositoryInjection {
	@Shadow
	@Final
	@Mutable
	private Set<RepositorySource> sources;

	@Override
	public synchronized void port_lib$addPackFinder(RepositorySource packFinder) {
		if (this.sources instanceof ImmutableSet<RepositorySource>)
			this.sources = new LinkedHashSet<>(this.sources);

		this.sources.add(packFinder);
	}
}
