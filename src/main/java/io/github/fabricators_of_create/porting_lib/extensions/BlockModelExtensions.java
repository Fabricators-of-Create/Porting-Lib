package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.model.BlockModelConfiguration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BlockModelExtensions {
  default BlockModelConfiguration getGeometry() {
	  throw new RuntimeException("this should be overridden via mixin. what?");
  }
}
