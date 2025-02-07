package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.util.math.AffineTransformation;

public interface ModelStateExtensions {
	default AffineTransformation getPartTransformation(Object part) {
		return AffineTransformation.identity();
	}
}
