package io.github.fabricators_of_create.porting_lib.extensions.extensions;

public interface VertexFormatExtensions {
	default int getOffset(int index) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
