package io.github.fabricators_of_create.porting_lib.entity.extensions;

import io.github.fabricators_of_create.porting_lib.entity.client.MobEffectRenderer;

import org.jetbrains.annotations.Nullable;

public interface MobEffectExtensions {
	@Nullable
	default MobEffectRenderer getRenderer() {
		return null;
	}
}
