package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.CustomLandingEffectsSlime;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Slime;

@Mixin(Slime.class)
public class SlimeMixin {
	@ModifyExpressionValue(
			method = "tick",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/monster/Slime;wasOnGround:Z",
					ordinal = 0
			)
	)
	private boolean onLand(boolean wasOnGround) {
		// if (onGround && !wasOnGround) { do landing stuff }
		// if this is called, onGround is true
		// !wasOnGround must be false to cancel, so wasOnGround must be true
		if (this instanceof CustomLandingEffectsSlime custom && custom.onLand())
			return true;
		return wasOnGround;
	}

	@ModifyExpressionValue(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "intValue=8"
			)
	)
	private int handleCustomParticles(int particlesToSpawn) {
		// for (int j = 0; j < i * 8; ++j) { spawn a particle }
		// wrapping the 8
		// replace with 0 to spawn no particles
		if (this instanceof CustomLandingEffectsSlime custom && custom.spawnLandingParticles())
			return 0;
		return particlesToSpawn;
	}

	@WrapWithCondition(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/Slime;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
			)
	)
	private boolean handleCustomSound(Slime self, SoundEvent squishSound, float volume, float pitch) {
		if (this instanceof CustomLandingEffectsSlime custom)
			return !custom.playLandingSound(squishSound, volume, pitch);
		return true;
	}
}
