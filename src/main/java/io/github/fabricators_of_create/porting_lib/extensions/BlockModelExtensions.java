package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.model.BlockModelConfiguration;

public interface BlockModelExtensions {

  default BlockModelConfiguration getGeometry() {
	  throw new RuntimeException("I think your kinda bad, this shouldn't happen!");
  }
}
