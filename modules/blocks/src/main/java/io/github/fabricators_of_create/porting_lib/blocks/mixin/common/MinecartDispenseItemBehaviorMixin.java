package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartDispenseItemBehavior.class)
public class MinecartDispenseItemBehaviorMixin {
	@Unique
	private static final ThreadLocal<ServerLevel> porting_lib$LEVEL = new ThreadLocal<>();
	@Unique
	private static final ThreadLocal<BlockPos> porting_lib$POS = new ThreadLocal<>();

	@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/MinecartDispenseItemBehavior;getRailShape(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/properties/RailShape;", ordinal = 0, shift = At.Shift.BEFORE))
	private void addGetRailShapeContext(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<ItemStack> cir, @Local ServerLevel level, @Local BlockPos pos) {
		porting_lib$LEVEL.set(level);
		porting_lib$POS.set(pos);
	}

	@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/MinecartDispenseItemBehavior;getRailShape(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/properties/RailShape;", ordinal = 1, shift = At.Shift.BEFORE))
	private void addGetRailShapeContextBelow(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<ItemStack> cir, @Local ServerLevel level, @Local BlockPos pos) {
		porting_lib$LEVEL.set(level);
		porting_lib$POS.set(pos.below());
	}

	@WrapOperation(method = "getRailShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private static Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock) {
			ServerLevel level = porting_lib$LEVEL.get();
			BlockPos pos = porting_lib$POS.get();
			porting_lib$LEVEL.remove();
			porting_lib$POS.remove();
			return railBlock.getRailDirection(instance, level, pos, null);
		}
		return original.call(instance, property);
	}
}
