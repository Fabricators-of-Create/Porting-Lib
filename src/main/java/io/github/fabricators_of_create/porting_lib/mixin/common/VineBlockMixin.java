package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.VineBlock;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements IShearable {
}
