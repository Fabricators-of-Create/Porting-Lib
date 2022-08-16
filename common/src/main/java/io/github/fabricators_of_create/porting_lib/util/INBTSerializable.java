package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {
	T serializeNBT();

	void deserializeNBT(T nbt);
}
