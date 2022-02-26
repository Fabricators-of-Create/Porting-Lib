package io.github.fabricators_of_create.porting_lib.util;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BaseRailBlockExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.AbstractMinecartAccessor;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartAndRailUtil {

	// rails

	public static final Tag.Named<Block> ACTIVATOR_RAILS = TagFactory.BLOCK.create(new ResourceLocation("c", "activator_rails"));

	public static boolean isActivatorRail(Block rail) {
		return ACTIVATOR_RAILS.contains(rail);
	}

	public static RailShape getDirectionOfRail(BlockState state, BlockGetter world, BlockPos pos, @Nullable BaseRailBlock block) {
		return ((BaseRailBlockExtensions) state.getBlock()).port_lib$getRailDirection(state, world, pos, block);
	}

	// carts

	public static void moveMinecartOnRail(AbstractMinecart cart, BlockPos pos) {
		((AbstractMinecartExtensions) cart).port_lib$moveMinecartOnRail(pos);
	}

	public static double getMaximumSpeed(AbstractMinecart cart) {
		return ((AbstractMinecartAccessor) cart).port_lib$getMaxSpeed();
	}

	public static boolean canCartUseRail(AbstractMinecart cart) {
		return ((AbstractMinecartExtensions) cart).port_lib$canUseRail();
	}

	public static BlockPos getExpectedRailPos(AbstractMinecart cart) {
		return ((AbstractMinecartExtensions) cart).port_lib$getCurrentRailPos();
	}

	public static double getSlopeAdjustment() {
		return 0.0078125D;
	}
}
