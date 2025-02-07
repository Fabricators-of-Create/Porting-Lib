package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.block.SeagrassBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SeagrassBlock.class)
public abstract class SeagrassBlockMixin implements IShearable {
}
