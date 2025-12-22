package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.MinecartCollisionContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecartCollisionContext.class)
public class MinecartCollisionContextMixin {
	@WrapOperation(method = "setupContext", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, AbstractMinecart minecart, @Local BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, minecart.level(), pos, minecart);
		return original.call(instance, property);
	}
}
