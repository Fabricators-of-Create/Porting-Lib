package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;
import net.minecraft.world.level.block.WebBlock;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(WebBlock.class)
public abstract class WebBlockMixin implements IShearable {
}
