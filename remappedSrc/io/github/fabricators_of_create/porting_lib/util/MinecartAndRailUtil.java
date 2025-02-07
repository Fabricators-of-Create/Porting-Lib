package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BaseRailBlockExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.AbstractMinecartAccessor;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class MinecartAndRailUtil {

	// rails

	public static final TagKey<Block> ACTIVATOR_RAILS = TagKey.create(Registry.BLOCK_KEY, new Identifier("c", "rails/activator"));

	public static boolean isActivatorRail(Block rail) {
		return rail.builtInRegistryHolder().is(ACTIVATOR_RAILS);
	}

	public static RailShape getDirectionOfRail(BlockState state, BlockView world, BlockPos pos, @Nullable AbstractRailBlock block) {
		return ((BaseRailBlockExtensions) state.getBlock()).getRailDirection(state, world, pos, block);
	}

	// carts

	public static double getMaximumSpeed(AbstractMinecartEntity cart) {
		return ((AbstractMinecartAccessor) cart).port_lib$getMaxSpeed();
	}

	public static double getSlopeAdjustment() {
		return 0.0078125D;
	}
}
