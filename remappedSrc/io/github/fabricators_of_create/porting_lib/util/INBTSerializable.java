package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.NbtElement;

public interface INBTSerializable<T extends NbtElement> {
	T serializeNBT();

	void deserializeNBT(T nbt);
}
