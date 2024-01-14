package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NBTSerializer {
	public static void deserializeNBT(Object o, Tag nbt) {
		((INBTSerializable) o).deserializeNBT(nbt);
	}

	public static Tag serializeNBT(Object o) {
		return ((INBTSerializable) o).serializeNBT();
	}

	public static CompoundTag serializeNBTCompound(Object o) {
		Tag tag = ((INBTSerializable) o).serializeNBT();
		if (tag instanceof CompoundTag c)
			return c;
		throw new RuntimeException("Cannot use serializeNBTCompound with a type that does not return a CompoundTag");
	}
}
