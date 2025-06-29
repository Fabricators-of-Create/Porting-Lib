package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomScaffoldingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@WrapWithCondition(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
			)
	)
	protected <T extends ParticleOptions> boolean updateFallState(
			ServerLevel instance, T type,
			double posX, double posY, double posZ,
			int particleCount, double xOffset, double yOffset, double zOffset, double speed,
			double y, boolean onGround, BlockState state, BlockPos pos,
			@Local(ordinal = 0) int numberOfParticles
	) {
		if (state.getBlock() instanceof CustomLandingEffectsBlock custom) {
			return !custom.addLandingEffects(state, (ServerLevel) level(), pos, state, (LivingEntity) (Object) this, numberOfParticles);
		}

		return true;
	}

	@ModifyExpressionValue(
			method = "handleOnClimbable",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
			)
	)
	private boolean customScaffoldingMovement(boolean original) {
		BlockState state = getInBlockState();
		if (state.getBlock() instanceof CustomScaffoldingBlock custom)
			return custom.isScaffolding(state, level(), blockPosition(), (LivingEntity) (Object) this);
		return original;
	}

	@WrapOperation(
			method = "travelInAir",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	public float port_lib$setSlipperiness(Block instance, Operation<Float> original, @Local BlockPos pos) {
		BlockState state = level().getBlockState(pos);
		if (instance instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level(), pos, (LivingEntity) (Object) this);
		}
		return original.call(instance);
	}
}
