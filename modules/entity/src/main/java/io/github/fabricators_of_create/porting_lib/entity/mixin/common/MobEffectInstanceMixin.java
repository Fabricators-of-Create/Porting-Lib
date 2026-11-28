package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import io.github.fabricators_of_create.porting_lib.entity.injects.MobEffectInstance$DetailsInjection;
import io.github.fabricators_of_create.porting_lib.entity.injects.MobEffectInstanceInjection;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin implements MobEffectInstanceInjection {
	@Shadow
	@Final
	private Holder<MobEffect> effect;

	@Unique
	private Supplier<Set<EffectCure>> porting_lib$cures = Suppliers.memoize(() -> {
		var set = Sets.<EffectCure>newIdentityHashSet();
		this.effect.value().fillEffectCures(set, MixinHelper.cast(this));
		return set;
	});

	/**
	 * {@return the {@link EffectCure}s which can cure the {@link MobEffect} held by this {@link MobEffectInstance}}
	 */
	public Set<EffectCure> getCures() {
		return porting_lib$cures.get();
	}

	@ModifyReturnValue(method = "asDetails", at = @At("RETURN"))
	private MobEffectInstance.Details addEffectsToDetails(MobEffectInstance.Details original) {
		((MobEffectInstance$DetailsInjection) (Object) original).port_lib$setCures(Optional.of(getCures()).filter(cures -> !cures.isEmpty()));
		return original;
	}

	@Inject(method = "setDetailsFrom", at = @At("TAIL"))
	private void copyEffects(MobEffectInstance effectInstance, CallbackInfo ci) {
		// Copy the cures eagerly into a mutable set: a lazy reference back to the source instance can
		// form a cycle when hidden effects are swapped around (each instance's cures reference the
		// other), which causes a StackOverflowError once the cures are first read.
		// Eager copying also keeps the set mutable: when loaded from NBT the cures are immutable
		// (PortingLibExtraCodecs.setOf decodes with ImmutableSet::copyOf) and clear() would throw.
		Set<EffectCure> cures = new HashSet<>(effectInstance.getCures());
		this.porting_lib$cures = Suppliers.memoize(() -> cures);
	}

	@Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/world/effect/MobEffectInstance$Details;)V", at = @At("TAIL"))
	private void loadEffects(Holder<MobEffect> effect, MobEffectInstance.Details details, CallbackInfo ci) {
		var detailsCures = ((MobEffectInstance$DetailsInjection) (Object) details).port_lib$getCures();
		detailsCures.ifPresent(set -> {
			// The set decoded from NBT/network is immutable (see PortingLibExtraCodecs.setOf), but getCures()
			// is documented to be modifiable, so keep a mutable copy instead of aliasing the decoded set.
			this.porting_lib$cures = Suppliers.memoize(() -> new HashSet<>(set));
		});
	}
}
