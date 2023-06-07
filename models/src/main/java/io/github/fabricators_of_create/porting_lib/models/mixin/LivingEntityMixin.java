package io.github.fabricators_of_create.porting_lib.models.mixin;

import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@ModifyArgs(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
	private void addPosToParticle(Args args, double y, boolean onGround, BlockState state, BlockPos pos) {
		((BlockParticleOptionExtensions)args.get(0)).setSourcePos(pos);
	}
}
