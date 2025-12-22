package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
	protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@WrapOperation(method = "adjustToRails", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), pos, this.minecart);
		}
		return original.call(instance, property);
	}

	@WrapOperation(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
	protected Comparable getRailShape2(BlockState instance, Property property, Operation<Comparable> original, @Local BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), pos, this.minecart);
		}
		return original.call(instance, property);
	}

	@WrapOperation(method = "stepAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(Lnet/minecraft/core/Position;)Lnet/minecraft/core/BlockPos;"))
	private BlockPos saveRailPos(Position position, Operation<BlockPos> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		BlockPos railPos = original.call(position);
		railposRef.set(railPos);
		return railPos;
	}

	@WrapOperation(method = "stepAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable getRailShape3(BlockState instance, Property property, Operation<Comparable> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), railposRef.get(), this.minecart);
		}
		return original.call(instance, property);
	}
}
