package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.block.DeadBushBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DeadBushBlock.class)
public abstract class DeadBushBlockMixin implements IShearable {
}
