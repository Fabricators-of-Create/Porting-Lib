package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Mixin(BoatEntity.class)
public abstract class BoatMixin extends Entity {
	// you can't capture locals in a @ModifyVariable, so we have this
	@Unique
	private BlockState port_lib$state;
	@Unique
	private BlockPos.Mutable port_lib$pos;

	public BoatMixin(EntityType<?> entityType, World level) {
		super(entityType, level);
	}

	@Inject(
			method = "getGroundFriction()F",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$storeVariables(CallbackInfoReturnable<Float> cir,
									Box aabb, Box aabb2, int i, int j, int k,
									int l, int m, int n, VoxelShape shape, float f, int o, BlockPos.Mutable mutable,
									int p, int q, int r, int s, BlockState blockState) {
		port_lib$state = blockState;
		port_lib$pos = mutable;
	}

	@ModifyVariable(
			method = "getGroundFriction",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float port_lib$setFriction(float original) {
		if (port_lib$state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(port_lib$state, world, port_lib$pos, this);
		}
		return original;
	}
}
