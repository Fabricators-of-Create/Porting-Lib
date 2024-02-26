package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import com.mojang.math.Transformation;

public interface ModelStateExtensions {
	default Transformation getPartTransformation(Object part) {
		return Transformation.identity();
	}
}
