package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.AddPackFindersCallback;
import io.github.fabricators_of_create.porting_lib.extensions.PackRepositoryExtension;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryExtension {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Override
	public synchronized void pl$addPackFinder(RepositorySource packFinder) {
		this.sources.add(packFinder);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void addModdedPacks(RepositorySource[] repositorySources, CallbackInfo ci) {
		AddPackFindersCallback.EVENT.invoker().addPack(this::pl$addPackFinder);
	}
}
