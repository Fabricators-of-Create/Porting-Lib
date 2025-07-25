package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLadderBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomScaffoldingBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.SupportsClimbableOpenTrapdoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos,
								   CallbackInfo ci, @Local(index = 19) int count) {
		if (state.getBlock() instanceof CustomLandingEffectsBlock custom &&
				custom.addLandingEffects(state, (ServerLevel) level(), pos, state, (LivingEntity) (Object) this, count)) {
			super.checkFallDamage(y, onGround, state, pos);
			ci.cancel();
		}
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

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(
			method = "travel",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")),
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	public float port_lib$setSlipperiness(float p) {
		BlockPos pos = getBlockPosBelowThatAffectsMyMovement();
		BlockState state = level().getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level(), pos, (LivingEntity) (Object) this);
		}
		return p;
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
