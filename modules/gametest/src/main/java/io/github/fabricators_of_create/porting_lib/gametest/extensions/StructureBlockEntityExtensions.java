package io.github.fabricators_of_create.porting_lib.gametest.extensions;

public interface StructureBlockEntityExtensions {
	default void setQualifiedTestName(String qualifiedTestName) {
		throw new AbstractMethodError();
	}

	default String getQualifiedTestName() {
		throw new AbstractMethodError();
	}
}
