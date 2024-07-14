package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.google.common.collect.Sets;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import io.github.fabricators_of_create.porting_lib.entity.ext.MobEffectInstanceExt;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin implements MobEffectInstanceExt {
	@Shadow
	@Final
	private Holder<MobEffect> effect;

	private final Set<EffectCure> porting_lib$cures = Sets.newIdentityHashSet();

	/**
	 * {@return the {@link EffectCure}s which can cure the {@link MobEffect} held by this {@link MobEffectInstance}}
	 */
	public Set<EffectCure> getCures() {
		return porting_lib$cures;
	}

	@Inject(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
	private void addEffects(Holder<MobEffect> holder, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon, MobEffectInstance hiddenEffect, CallbackInfo ci) {
		this.effect.value().fillEffectCures(this.porting_lib$cures, MixinHelper.cast(this));
	}
}
