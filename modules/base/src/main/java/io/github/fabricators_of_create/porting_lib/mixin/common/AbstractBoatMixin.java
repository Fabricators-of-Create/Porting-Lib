package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomFrictionBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatMixin extends Entity {
	public AbstractBoatMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@WrapOperation(
			method = "getGroundFriction",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float getCustomFriction(Block instance, Operation<Float> original, @Local BlockState state, @Local BlockPos.MutableBlockPos pos) {
		if (instance instanceof CustomFrictionBlock custom)
			return custom.getFriction(state, level(), pos, this);
		return original.call(instance);
	}
}
