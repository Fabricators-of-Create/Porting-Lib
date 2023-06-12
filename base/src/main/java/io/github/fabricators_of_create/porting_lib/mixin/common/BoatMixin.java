package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity {
	// you can't capture locals in a @ModifyVariable, so we have this
	@Unique
	private BlockState port_lib$state;
	@Unique
	private BlockPos.MutableBlockPos port_lib$pos;

	public BoatMixin(EntityType<?> entityType, Level level) {
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
									AABB aabb, AABB aabb2, int i, int j, int k,
									int l, int m, int n, VoxelShape shape, float f, int o, BlockPos.MutableBlockPos mutable,
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
			return custom.getFriction(port_lib$state, level(), port_lib$pos, this);
		}
		return original;
	}
}
