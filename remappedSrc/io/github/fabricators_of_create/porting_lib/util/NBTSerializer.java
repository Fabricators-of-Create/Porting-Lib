package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class NBTSerializer {
	public static void deserializeNBT(Object o, NbtElement nbt) {
		((INBTSerializable) o).deserializeNBT(nbt);
	}

	public static NbtElement serializeNBT(Object o) {
		return ((INBTSerializable) o).serializeNBT();
	}

	public static NbtCompound serializeNBTCompound(Object o) {
		NbtElement tag = ((INBTSerializable) o).serializeNBT();
		if (tag instanceof NbtCompound c)
			return c;
		throw new RuntimeException("Cannot use serializeNBTCompound with a type that does not return a CompoundTag");
	}
}
