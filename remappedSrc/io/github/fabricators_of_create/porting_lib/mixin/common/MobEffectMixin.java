package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.MobEffectExtensions;
import net.minecraft.entity.effect.StatusEffect;

@Mixin(StatusEffect.class)
public class MobEffectMixin implements MobEffectExtensions {
}
