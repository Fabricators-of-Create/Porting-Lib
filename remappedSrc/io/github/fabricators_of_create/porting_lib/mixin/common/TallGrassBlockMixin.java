package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.block.FernBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FernBlock.class)
public abstract class TallGrassBlockMixin implements IShearable {
}
