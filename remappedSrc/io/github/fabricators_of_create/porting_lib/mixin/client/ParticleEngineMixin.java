package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.block.CustomDestroyEffectsBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public abstract class ParticleEngineMixin {
	@Shadow
	protected ClientWorld level;

	@Shadow
	@Final
	@Mutable
	private static List<ParticleTextureSheet> RENDER_ORDER;

	@Unique
	private static boolean port_lib$replacedRenderOrderList = false;

	private static void port_lib$addRenderType(ParticleTextureSheet type) {
		if (!port_lib$replacedRenderOrderList) {
			List<ParticleTextureSheet> old = RENDER_ORDER;
			RENDER_ORDER = new ArrayList<>(old);
			port_lib$replacedRenderOrderList = true;
		}
		RENDER_ORDER.add(type);
	}

	@Inject(method = "method_18125", at = @At("RETURN"))
	private static void port_lib$addCustomRenderTypes(ParticleTextureSheet particleRenderType, CallbackInfoReturnable<Queue<Particle>> cir) {
		if (!RENDER_ORDER.contains(particleRenderType)) {
			port_lib$addRenderType(particleRenderType);
		}
	}

	@Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
	private void port_lib$customDestroyEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.getBlock() instanceof CustomDestroyEffectsBlock custom) {
			if (custom.applyCustomDestroyEffects(blockState, level, blockPos, (ParticleManager) (Object) this)) {
				ci.cancel();
			}
		}
	}

	@ModifyArgs(
			method = "crack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;add(Lnet/minecraft/client/particle/Particle;)V"
			)
	)
	private void updateSpriteOnCrack(Args args, BlockPos pos, Direction side, @Local(ordinal = 0) BlockState state) {
		Particle particle = args.get(0);
		if (particle instanceof BlockDustParticle terrainParticle)
			terrainParticle.updateSprite(state, pos);
	}

	@ModifyArgs(
			method = { "method_34020", "lambda$destroy$11", "m_ckteflwv" },
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;add(Lnet/minecraft/client/particle/Particle;)V"
			)
	)
	private void updateSpriteOnDestroy(Args args, BlockPos pos, BlockState state,
									   double dx, double e, double f, double g, double h, double i) {
		Particle particle = args.get(0);
		if (particle instanceof BlockDustParticle terrainParticle)
			terrainParticle.updateSprite(state, pos);
	}
}
