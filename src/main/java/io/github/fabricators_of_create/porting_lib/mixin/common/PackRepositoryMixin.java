package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V", at = @At("TAIL"))
	public void port_lib$addModdedPacks(PackType packType, RepositorySource[] repositorySources, CallbackInfo ci) {

	}
}
