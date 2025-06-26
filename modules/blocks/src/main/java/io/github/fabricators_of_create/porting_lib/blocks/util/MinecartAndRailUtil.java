package io.github.fabricators_of_create.porting_lib.blocks.util;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import io.github.fabricators_of_create.porting_lib.blocks.mixin.common.AbstractMinecartAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartAndRailUtil {

	// rails

	public static final TagKey<Block> ACTIVATOR_RAILS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "rails/activator"));

	public static boolean isActivatorRail(Block rail) {
		return rail.builtInRegistryHolder().is(ACTIVATOR_RAILS);
	}

	public static RailShape getDirectionOfRail(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart minecart) {
		if (state.getBlock() instanceof CustomRailDirectionBlock block) {
			return block.getRailDirection(state, world, pos, minecart);
		}
		return state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
	}

	// carts

	public static double getMaximumSpeed(AbstractMinecart cart) {
		return ((AbstractMinecartAccessor) cart).port_lib$getMaxSpeed();
	}

	public static double getSlopeAdjustment() {
		return 0.0078125D;
	}
}
