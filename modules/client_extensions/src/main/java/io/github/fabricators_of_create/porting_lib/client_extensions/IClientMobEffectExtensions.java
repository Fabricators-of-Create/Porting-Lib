package io.github.fabricators_of_create.porting_lib.client_extensions;

import net.fabricmc.api.EnvType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * {@linkplain EnvType#CLIENT Client-only} extensions to {@link MobEffect}.
 */
public interface IClientMobEffectExtensions {
	IClientMobEffectExtensions DEFAULT = new IClientMobEffectExtensions() {};

	static IClientMobEffectExtensions of(MobEffectInstance instance) {
		return of(instance.getEffect().value());
	}

	static IClientMobEffectExtensions of(MobEffect effect) {
		return ClientExtensionsRegistry.MOB_EFFECT_EXTENSIONS.getOrDefault(effect, DEFAULT);
	}
}
