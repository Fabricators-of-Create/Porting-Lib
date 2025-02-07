package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.AddPackFindersCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;

@Mixin(ResourcePackManager.class)
public abstract class PackRepositoryMixin {
	@Shadow
	@Final
	private Set<ResourcePackProvider> sources;

	@Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V", at = @At("TAIL"))
	public void port_lib$addModdedPacks(ResourceType packType, ResourcePackProvider[] repositorySources, CallbackInfo ci) {
		AddPackFindersCallback.EVENT.invoker().addPack(packType, sources::add);
	}
}
