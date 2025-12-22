package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.SlopeCreationCheckingRailBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(RailState.class)
public abstract class RailStateMixin {

	@Final
	@Shadow
	private BaseRailBlock block;

	@Unique
	private boolean porting_lib$canMakeSlopes = true;

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, Level level, BlockPos pos, BlockState state) {
		if (this.block instanceof SlopeCreationCheckingRailBlock railBlock)
			this.porting_lib$canMakeSlopes = railBlock.canMakeSlopes(state, level, pos);
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = { "connectTo", "place" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseRailBlock;isRail(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean port_lib$wrapRailChecksToCheckSlopes(Level level, BlockPos blockPos, Operation<Boolean> original) {
		return original.call(level, blockPos) && porting_lib$canMakeSlopes;
	}

}
