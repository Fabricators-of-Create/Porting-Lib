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
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OldMinecartBehavior.class)
public abstract class OldMinecartBehaviorMixin extends MinecartBehavior {
	protected OldMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
		super(abstractMinecart);
	}

	@WrapOperation(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
	protected Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, @Local BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), pos, this.minecart);
		}
		return original.call(instance, property);
	}

	@WrapOperation(method = "getPosOffs", at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;", ordinal = 1))
	private BlockPos saveRailPos(int x, int y, int z, Operation<BlockPos> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		BlockPos railPos = original.call(x, y, z);
		railposRef.set(railPos);
		return railPos;
	}

	@WrapOperation(method = "getPosOffs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable getRailShape2(BlockState instance, Property property, Operation<Comparable> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), railposRef.get(), this.minecart);
		}
		return original.call(instance, property);
	}

	@WrapOperation(method = "getPos", at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;", ordinal = 1))
	private BlockPos saveRailPos2(int x, int y, int z, Operation<BlockPos> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		BlockPos railPos = original.call(x, y, z);
		railposRef.set(railPos);
		return railPos;
	}

	@WrapOperation(method = "getPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable getRailShape3(BlockState instance, Property property, Operation<Comparable> original, @Share("railpos") LocalRef<BlockPos> railposRef) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			return railBlock.getRailDirection(instance, level(), railposRef.get(), this.minecart);
		}
		return original.call(instance, property);
	}
}
