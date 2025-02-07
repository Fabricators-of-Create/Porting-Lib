package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

public interface BlockEntityExtensions {
	default NbtCompound getExtraCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void deserializeNBT(BlockState state, NbtCompound nbt) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void onLoad() {
	}

	default void invalidateCaps() {
	}
}
