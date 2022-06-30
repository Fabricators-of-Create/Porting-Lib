package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.nbt.Tag;

// can't inject this because generics
public interface INBTSerializable<T extends Tag> {
	T serializeNBT();

	void deserializeNBT(T nbt);
}
