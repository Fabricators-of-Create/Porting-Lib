package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
	CompoundTag port_lib$serializeNBT();

	void port_lib$deserializeNBT(CompoundTag nbt);
}
