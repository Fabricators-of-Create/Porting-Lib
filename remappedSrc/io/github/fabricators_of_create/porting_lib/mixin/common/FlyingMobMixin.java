package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(FlyingEntity.class)
public abstract class FlyingMobMixin extends MobEntity {
	protected FlyingMobMixin(EntityType<? extends MobEntity> entityType, World level) {
		super(entityType, level);
	}

	@ModifyVariable(
			method = "travel",																					// first call		// first float variable
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F", ordinal = 0), ordinal = 0
	)
	private float port_lib$setFriction(float original) {
		return port_lib$handleFriction(original);
	}

	@ModifyVariable(
			method = "travel",																					// second call		// first float variable
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F", ordinal = 1), ordinal = 0
	)
	private float port_lib$setFriction2(float original) {
		return port_lib$handleFriction(original);
	}

	private float port_lib$handleFriction(float original) {
		BlockPos pos = new BlockPos(getX(), getY() - 1, getZ());
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, world, pos, this);
		}
		return original;
	}
}
