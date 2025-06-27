package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.VanillaIShearable;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.animal.SnowGolem;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin implements VanillaIShearable {
}
