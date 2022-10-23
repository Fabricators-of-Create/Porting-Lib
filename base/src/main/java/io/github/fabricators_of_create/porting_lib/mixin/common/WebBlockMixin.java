package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.WebBlock;

@Mixin(WebBlock.class)
public abstract class WebBlockMixin implements IShearable {
}
