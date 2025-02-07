package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.block.VineBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements IShearable {
}
