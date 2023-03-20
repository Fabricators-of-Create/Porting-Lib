package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.DimensionSpecialEffectsExtensions;

import net.minecraft.client.renderer.DimensionSpecialEffects;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin implements DimensionSpecialEffectsExtensions {
}
