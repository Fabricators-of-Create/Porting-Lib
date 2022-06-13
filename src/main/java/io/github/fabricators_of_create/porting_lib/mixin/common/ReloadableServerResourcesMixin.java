package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.TagsUpdatedCallback;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At("TAIL"))
	public void port_lib$updateTags(RegistryAccess dynamicRegistryManager, CallbackInfo ci) {
		TagsUpdatedCallback.EVENT.invoker().onTagsUpdated(dynamicRegistryManager);
	}
}
