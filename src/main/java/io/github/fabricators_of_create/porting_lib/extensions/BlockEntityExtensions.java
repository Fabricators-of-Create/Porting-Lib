package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityExtensions {
	CompoundTag getExtraCustomData();

	void deserializeNBT(BlockState state, CompoundTag nbt);
}
