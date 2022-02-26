package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.nbt.CompoundTag;

public interface CustomUpdateTagHandlingBlockEntity {
	void handleUpdateTag(CompoundTag tag);
}
