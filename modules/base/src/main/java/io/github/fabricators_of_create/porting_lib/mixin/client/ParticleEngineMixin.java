package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.block.CustomDestroyEffectsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Shadow
	protected ClientLevel level;

	@Shadow
	@Final
	@Mutable
	private static List<ParticleRenderType> RENDER_ORDER;

	@Unique
	private static void addRenderTypeSafe(ParticleRenderType type) {
		if (!(RENDER_ORDER instanceof ArrayList)) {
			List<ParticleRenderType> old = RENDER_ORDER;
			RENDER_ORDER = new ArrayList<>(old);
		}
		RENDER_ORDER.add(type);
	}

	@Inject(method = "method_18125", at = @At("RETURN"))
	private static void addCustomRenderTypes(ParticleRenderType particleRenderType, CallbackInfoReturnable<Queue<Particle>> cir) {
		if (!RENDER_ORDER.contains(particleRenderType)) {
			addRenderTypeSafe(particleRenderType);
		}
	}

	@ModifyExpressionValue(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"))
	private boolean customDestroyEffects(boolean original, BlockPos blockPos, BlockState blockState) {
		if (blockState.getBlock() instanceof CustomDestroyEffectsBlock custom) {
			if (!custom.addDestroyEffects(blockState, level, blockPos, (ParticleEngine) (Object) this)) {
				return false;
			}
		}
		return original;
	}
}
