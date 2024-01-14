package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;
import net.minecraft.world.level.block.VineBlock;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements IShearable {
}
