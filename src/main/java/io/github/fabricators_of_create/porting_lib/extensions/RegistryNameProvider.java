package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface RegistryNameProvider {
  default ResourceLocation getRegistryName() {
	  throw new RuntimeException("this should be overridden via mixin. what?");
  }
}
