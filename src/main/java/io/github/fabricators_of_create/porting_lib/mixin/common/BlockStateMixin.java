package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.BlockStateExtensions;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class BlockStateMixin implements BlockStateExtensions {
	// This space for rent (DO NOT DELETE THIS MIXIN, THE INTERFACE IMPLEMENTATION IS IMPORTANT)
}
