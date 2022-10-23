package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.MobEffectExtensions;
import net.minecraft.world.effect.MobEffect;

@Mixin(MobEffect.class)
public class MobEffectMixin implements MobEffectExtensions {
}
