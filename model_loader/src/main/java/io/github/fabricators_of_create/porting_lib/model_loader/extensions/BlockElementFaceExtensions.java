package io.github.fabricators_of_create.porting_lib.model_loader.extensions;

public interface BlockElementFaceExtensions {
	default void setEmissivity(int emissivity) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default int getEmissivity() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
