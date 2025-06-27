package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.VanillaIShearable;
import net.minecraft.world.entity.monster.Bogged;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Bogged.class)
public class BoggedMixin implements VanillaIShearable {
}
