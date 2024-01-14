package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(PackRepository.class)
public interface PackRepositoryAccessor {
	@Accessor
	Set<RepositorySource> getSources();
}
