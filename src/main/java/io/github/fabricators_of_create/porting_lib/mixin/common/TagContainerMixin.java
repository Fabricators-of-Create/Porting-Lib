package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.TagsUpdatedCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tags.TagContainer;

@Mixin(TagContainer.class)
public abstract class TagContainerMixin {
  @Inject(method = "bindToGlobal", at = @At("TAIL"))
  public void port_lib$tabBindToGlobal(CallbackInfo ci) {
    TagsUpdatedCallback.EVENT.invoker().onTagsUpdated((TagContainer) (Object) this);
  }
}
