package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FlyingMob.class)
public abstract class FlyingMobMixin extends Mob {
	protected FlyingMobMixin(EntityType<? extends Mob> entityType, Level level) {
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
		BlockPos pos = BlockPos.containing(getX(), getY() - 1, getZ());
		BlockState state = level().getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level(), pos, this);
		}
		return original;
	}
}
