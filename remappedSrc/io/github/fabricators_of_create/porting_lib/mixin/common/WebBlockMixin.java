package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import net.minecraft.block.CobwebBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CobwebBlock.class)
public abstract class WebBlockMixin implements IShearable {
}
