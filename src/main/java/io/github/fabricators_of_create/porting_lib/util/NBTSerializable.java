package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
	CompoundTag serializeNBT();

	void deserializeNBT(CompoundTag nbt);
}
