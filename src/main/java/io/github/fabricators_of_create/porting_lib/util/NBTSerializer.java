package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.Tag;

public class NBTSerializer {
	public static void deserializeNBT(Object o, Tag nbt) {
		((INBTSerializable) o).deserializeNBT(nbt);
	}

	public static Tag serializeNBT(Object o) {
		return ((INBTSerializable) o).serializeNBT();
	}
}
