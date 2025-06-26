package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.MinecartPassHandlerBlock;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity {
	private AbstractMinecartMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 4))
	protected void onMoveAlongTrack(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.getBlock() instanceof MinecartPassHandlerBlock handler) {
			handler.onMinecartPass(blockState, level(), blockPos, (AbstractMinecart) (Object) this);
		}
	}

	@ModifyExpressionValue(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
	protected Comparable<?> getRailShape(Comparable<?> original, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CustomRailDirectionBlock block) {
			return block.getRailDirection(state, level(), pos, (AbstractMinecart) (Object) this);
		}
		return original;
	}

	@ModifyExpressionValue(method = "getPosOffs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable<?> getRailShape1(Comparable original, double x, double y, double z, @Local BlockState state) {
		if (state.getBlock() instanceof CustomRailDirectionBlock block) {
			return block.getRailDirection(state, level(), new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), (AbstractMinecart) (Object) this);
		}
		return original;
	}

	@ModifyExpressionValue(method = "getPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	protected Comparable<?> getRailShape2(Comparable original, double x, double y, double z, @Local BlockState state) {
		if (state.getBlock() instanceof CustomRailDirectionBlock block) {
			return block.getRailDirection(state, level(), new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), (AbstractMinecart) (Object) this);
		}
		return original;
	}
}
