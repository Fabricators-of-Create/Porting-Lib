package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.fabricmc.fabric.mixin.content.registry.ShovelItemAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.Optional;

public interface BlockExtensions {
	default boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.relative(facing));
		PlantType type = plantable.getPlantType(world, pos.relative(facing));

		if (plant.getBlock() == Blocks.CACTUS)
			return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);

		if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
			return true;

		if (plantable instanceof BushBlock && ((BushBlock)plantable).mayPlaceOn(state, world, pos))
			return true;

		if (PlantType.DESERT.equals(type)) {
			return this == Blocks.SAND || this == Blocks.TERRACOTTA || this instanceof GlazedTerracottaBlock;
		} else if (PlantType.NETHER.equals(type)) {
			return this == Blocks.SOUL_SAND;
		} else if (PlantType.CROP.equals(type)) {
			return state.is(Blocks.FARMLAND);
		} else if (PlantType.CAVE.equals(type)) {
			return state.isFaceSturdy(world, pos, Direction.UP);
		} else if (PlantType.PLAINS.equals(type)) {
			return this == Blocks.GRASS_BLOCK || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || this == Blocks.FARMLAND;
		} else if (PlantType.WATER.equals(type)) {
			return state.getMaterial() == net.minecraft.world.level.material.Material.WATER; //&& state.getValue(BlockLiquidWrapper)
		} else if (PlantType.BEACH.equals(type)) {
			boolean isBeach = state.is(Blocks.GRASS_BLOCK) || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
			boolean hasWater = false;
			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = world.getBlockState(pos.relative(face));
				net.minecraft.world.level.material.FluidState fluidState = world.getFluidState(pos.relative(face));
				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
				if (hasWater)
					break; //No point continuing.
			}
			return isBeach && hasWater;
		}
		return false;
	}

	/**
	 * Returns the state that this block should transform into when right clicked by a tool.
	 * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
	 * Return null if vanilla behavior should be disabled.
	 *
	 * @param state The current state
	 * @param world The world
	 * @param pos The block position in world
	 * @param player The player clicking the block
	 * @param stack The stack being used by the player
	 * @param toolAction The action being performed by the tool
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
		if (!stack.canPerformAction(toolAction)) return null;
		if (ToolActions.AXE_STRIP.equals(toolAction)) return AxeItem.STRIPPABLES.get(state.getBlock()).defaultBlockState();
		else if(ToolActions.AXE_SCRAPE.equals(toolAction)) return WeatheringCopper.getPrevious(state).orElse(null);
		else if(ToolActions.AXE_WAX_OFF.equals(toolAction)) return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((p_150694_) -> {
			return p_150694_.withPropertiesOf(state);
		}).orElse(null);
			//else if(ToolActions.HOE_TILL.equals(toolAction)) return HoeItem.getHoeTillingState(state); //TODO HoeItem bork
		else if (ToolActions.SHOVEL_FLATTEN.equals(toolAction)) return ShovelItem.FLATTENABLES.get(state.getBlock());
		return null;
	}
}
