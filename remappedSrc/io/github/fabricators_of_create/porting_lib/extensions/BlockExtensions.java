package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.client.ClientExtensionHooks;
import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.mixin.content.registry.ShovelItemAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface BlockExtensions {
	default boolean canSustainPlant(BlockState state, BlockView world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.offset(facing));
		PlantType type = plantable.getPlantType(world, pos.offset(facing));

		if (plant.getBlock() == Blocks.CACTUS)
			return state.isOf(Blocks.CACTUS) || state.isOf(Blocks.SAND) || state.isOf(Blocks.RED_SAND);

		if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
			return true;

		if (plantable instanceof PlantBlock && ((PlantBlock)plantable).canPlantOnTop(state, world, pos))
			return true;

		if (PlantType.DESERT.equals(type)) {
			return this == Blocks.SAND || this == Blocks.TERRACOTTA || this instanceof GlazedTerracottaBlock;
		} else if (PlantType.NETHER.equals(type)) {
			return this == Blocks.SOUL_SAND;
		} else if (PlantType.CROP.equals(type)) {
			return state.isOf(Blocks.FARMLAND);
		} else if (PlantType.CAVE.equals(type)) {
			return state.isSideSolidFullSquare(world, pos, Direction.UP);
		} else if (PlantType.PLAINS.equals(type)) {
			return this == Blocks.GRASS_BLOCK || ((Block)this).getDefaultState().isIn(BlockTags.DIRT) || this == Blocks.FARMLAND;
		} else if (PlantType.WATER.equals(type)) {
			return state.getMaterial() == net.minecraft.block.Material.WATER; //&& state.getValue(BlockLiquidWrapper)
		} else if (PlantType.BEACH.equals(type)) {
			boolean isBeach = state.isOf(Blocks.GRASS_BLOCK) || ((Block)this).getDefaultState().isIn(BlockTags.DIRT) || state.isOf(Blocks.SAND) || state.isOf(Blocks.RED_SAND);
			boolean hasWater = false;
			for (Direction face : Direction.Type.HORIZONTAL) {
				BlockState blockState = world.getBlockState(pos.offset(face));
				net.minecraft.fluid.FluidState fluidState = world.getFluidState(pos.offset(face));
				hasWater |= blockState.isOf(Blocks.FROSTED_ICE);
				hasWater |= fluidState.isIn(net.minecraft.tag.FluidTags.WATER);
				if (hasWater)
					break; //No point continuing.
			}
			return isBeach && hasWater;
		}
		return false;
	}

	/**
	 * Returns the state that this block should transform into when right-clicked by a tool.
	 * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
	 * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
	 * Returns {@code null} if nothing should happen.
	 *
	 * @param state The current state
	 * @param context The use on context that the action was performed in
	 * @param toolAction The action being performed by the tool
	 * @param simulate If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(BlockState state, ItemUsageContext context, ToolAction toolAction, boolean simulate) {
		BlockState toolModifiedState = getToolModifiedState(state, context.getWorld(), context.getBlockPos(),
				context.getPlayer(), context.getStack(), toolAction);

		if (toolModifiedState == null && ToolActions.HOE_TILL == toolAction && context.getStack().canPerformAction(ToolActions.HOE_TILL)) {
			// Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
			Block block = state.getBlock();
			if (block == Blocks.ROOTED_DIRT) {
				if (!simulate && !context.getWorld().isClient) {
					Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(Items.HANGING_ROOTS));
				}
				return Blocks.DIRT.getDefaultState();
			} else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
					context.getWorld().getBlockState(context.getBlockPos().up()).isAir()) {
				return block == Blocks.COARSE_DIRT ? Blocks.DIRT.getDefaultState() : Blocks.FARMLAND.getDefaultState();
			}
		}

		return toolModifiedState;
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
	default BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolAction toolAction) {
		if (!stack.canPerformAction(toolAction)) return null;
		if (ToolActions.AXE_STRIP.equals(toolAction)) {
			Block block = AxeItem.STRIPPED_BLOCKS.get(state.getBlock());
			return block != null ? block.getDefaultState() : null;
		}
		else if(ToolActions.AXE_SCRAPE.equals(toolAction)) return Oxidizable.getDecreasedOxidationState(state).orElse(null);
		else if(ToolActions.AXE_WAX_OFF.equals(toolAction)) return Optional.ofNullable(HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(state.getBlock())).map((p_150694_) -> {
			return p_150694_.getStateWithProperties(state);
		}).orElse(null);
			//else if(ToolActions.HOE_TILL.equals(toolAction)) return HoeItem.getHoeTillingState(state); //TODO HoeItem bork
		else if (ToolActions.SHOVEL_FLATTEN.equals(toolAction)) return ShovelItem.PATH_STATES.get(state.getBlock());
		return null;
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 * <p>
	 * This method should only be used for blocks you don't control, for your own blocks override
	 * {@link Block#isSideInvisible(BlockState, BlockState, Direction)} on the respective block instead
	 * <p>
	 * WARNING: This method is likely to be called from a worker thread! If you want to retrieve a
	 *          {@link net.minecraft.block.entity.BlockEntity} from the given level, make sure to use
	 *          {@link net.minecraftforge.common.extensions.IForgeBlockGetter#getExistingBlockEntity(BlockPos)} to not
	 *          accidentally create a new or delete an old {@link net.minecraft.block.entity.BlockEntity}
	 *          off of the main thread as this would cause a write operation to the given {@link BlockView} and cause
	 *          a CME in the process. Any other direct or indirect write operation to the {@link BlockView} will have
	 *          the same outcome.
	 *
	 * @param level The world
	 * @param pos The blocks position in the world
	 * @param state The blocks {@link BlockState}
	 * @param neighborState The neighboring blocks {@link BlockState}
	 * @param dir The direction towards the neighboring block
	 */
	default boolean hidesNeighborFace(BlockView level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return false;
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link BlockStateExtensions#hidesNeighborFace(BlockView, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean supportsExternalFaceHiding(BlockState state) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ClientExtensionHooks.isBlockInSolidLayer(state);
		}
		return true;
	}
}
