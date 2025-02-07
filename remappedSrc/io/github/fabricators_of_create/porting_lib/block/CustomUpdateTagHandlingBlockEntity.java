package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public interface CustomUpdateTagHandlingBlockEntity {
	default void handleUpdateTag(NbtCompound tag) {
		((BlockEntity) this).readNbt(tag);
	}
}
