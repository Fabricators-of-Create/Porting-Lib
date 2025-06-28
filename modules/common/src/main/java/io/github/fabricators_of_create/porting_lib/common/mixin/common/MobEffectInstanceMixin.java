package io.github.fabricators_of_create.porting_lib.common.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.common.extensions.CustomSortOrderMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
	@WrapOperation(method = "compareTo(Lnet/minecraft/world/effect/MobEffectInstance;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;getColor()I", ordinal = 0))
	private int getSortOrder1(MobEffect instance, Operation<Integer> original) {
		if (instance instanceof CustomSortOrderMobEffect custom)
			return custom.getSortOrder((MobEffectInstance) (Object) this);
		return original.call(instance);
	}

	@WrapOperation(method = "compareTo(Lnet/minecraft/world/effect/MobEffectInstance;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;getColor()I", ordinal = 2))
	private int getSortOrder2(MobEffect instance, Operation<Integer> original) {
		if (instance instanceof CustomSortOrderMobEffect custom)
			return custom.getSortOrder((MobEffectInstance) (Object) this);
		return original.call(instance);
	}

	@WrapOperation(method = "compareTo(Lnet/minecraft/world/effect/MobEffectInstance;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;getColor()I", ordinal = 1))
	private int getSortOrder1(MobEffect instance, Operation<Integer> original, MobEffectInstance other) {
		if (other.getEffect().value() instanceof CustomSortOrderMobEffect custom)
			return custom.getSortOrder(other);
		return original.call(instance);
	}

	@WrapOperation(method = "compareTo(Lnet/minecraft/world/effect/MobEffectInstance;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;getColor()I", ordinal = 3))
	private int getSortOrder2(MobEffect instance, Operation<Integer> original, MobEffectInstance other) {
		if (other.getEffect().value() instanceof CustomSortOrderMobEffect custom)
			return custom.getSortOrder(other);
		return original.call(instance);
	}
}
