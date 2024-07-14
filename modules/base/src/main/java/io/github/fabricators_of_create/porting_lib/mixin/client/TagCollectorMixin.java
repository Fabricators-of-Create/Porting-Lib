package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.common.TagsUpdatedEvent;
import net.minecraft.client.multiplayer.TagCollector;

import net.minecraft.core.RegistryAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TagCollector.class)
public class TagCollectorMixin {
	@Inject(method = "updateTags", at = @At("TAIL"))
	private void onUpdateTags(RegistryAccess registryAccess, boolean isIntegratedServerConnection, CallbackInfo ci) {
		new TagsUpdatedEvent(registryAccess, true, isIntegratedServerConnection).sendEvent();
	}
}
