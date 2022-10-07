package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.nbt.Tag;

// can't inject this because generics
public interface INBTSerializable<T extends Tag> extends io.github.fabricators_of_create.porting_lib.util.INBTSerializable<T> {
	default T serializeNBT() {
		throw new RuntimeException("override serializeNBT!");
	}

	default void deserializeNBT(T nbt) {
		throw new RuntimeException("override deserializeNBT!");
	}
}
