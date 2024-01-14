package io.github.fabricators_of_create.porting_lib.tool.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.tool.extensions.BlockExtensions;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public class BlockMixin implements BlockExtensions {
}
