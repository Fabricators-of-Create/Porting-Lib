package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity {
	private AbstractMinecartMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@WrapOperation(method = "getRedstoneDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
	private Comparable<?> getRailShape(BlockState instance, Property property, Operation<Comparable> original, BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock block) {
			return block.getRailDirection(instance, level(), pos, (AbstractMinecart) (Object) this);
		}
		return original.call(instance, property);
	}
}
