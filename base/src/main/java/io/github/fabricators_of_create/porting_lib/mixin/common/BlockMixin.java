package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockExtensions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements BlockExtensions {
	private BlockMixin(BlockBehaviour.Properties properties) {
		super(properties);
	}
}
