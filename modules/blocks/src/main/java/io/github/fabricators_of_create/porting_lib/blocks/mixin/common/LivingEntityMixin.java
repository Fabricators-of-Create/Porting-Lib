package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLadderBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomScaffoldingBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.SupportsClimbableOpenTrapdoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
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
	protected <T extends ParticleOptions> boolean updateFallState(ServerLevel instance, T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPos pos) {
		if (state.getBlock() instanceof CustomLandingEffectsBlock custom)
			return !custom.addLandingEffects(state, instance, pos, state, (LivingEntity) (Object) this, particleCount);
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

	@WrapOperation(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
	public float getCustomFriction(Block instance, Operation<Float> original, @Local BlockPos pos) {
		if (instance instanceof CustomFrictionBlock custom) {
			BlockState state = level().getBlockState(pos);
			return custom.getFriction(state, level(), pos, (LivingEntity) (Object) this);
		}
		return original.call(instance);
	}

	@WrapOperation(method = "onClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
	private boolean port_lib$tryUseCustomLadder(BlockState instance, TagKey tagKey, Operation<Boolean> original, @Local BlockPos pos) {
		if (instance.getBlock() instanceof CustomLadderBlock ladderBlock) {
			return ladderBlock.isLadder(instance, level(), pos, (LivingEntity) (Object) this);
		}

		return original.call(instance, tagKey);
	}

	@ModifyReturnValue(method = "trapdoorUsableAsLadder", at = @At(value = "RETURN", ordinal = 1))
	private boolean port_lib$tryCheckCanClimbTrapdoor(boolean original, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state) {
		BlockPos belowPos = pos.below();
		BlockState belowState = this.level().getBlockState(belowPos); // TODO: Can't access the local one, for some reason...

		if (belowState.getBlock() instanceof SupportsClimbableOpenTrapdoorBlock climbableOpenTrapdoorBlock) {
			return climbableOpenTrapdoorBlock.makesOpenTrapdoorAboveClimbable(belowState, this.level(), belowPos, state);
		}

		return original;
	}
}
