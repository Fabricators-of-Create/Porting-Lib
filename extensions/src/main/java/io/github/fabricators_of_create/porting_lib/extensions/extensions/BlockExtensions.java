package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import io.github.fabricators_of_create.porting_lib.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.common.util.PlantType;
import io.github.fabricators_of_create.porting_lib.common.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.common.util.ToolActions;
import io.github.fabricators_of_create.porting_lib.extensions.ClientExtensionHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

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
	default BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
		BlockState toolModifiedState = getToolModifiedState(state, context.getLevel(), context.getClickedPos(),
				context.getPlayer(), context.getItemInHand(), toolAction);

		if (toolModifiedState == null && ToolActions.HOE_TILL == toolAction && context.getItemInHand().canPerformAction(ToolActions.HOE_TILL)) {
			// Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
			Block block = state.getBlock();
			if (block == Blocks.ROOTED_DIRT) {
				if (!simulate && !context.getLevel().isClientSide) {
					Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
				}
				return Blocks.DIRT.defaultBlockState();
			} else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
					context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
				return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
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
	default BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
		if (!stack.canPerformAction(toolAction)) return null;
		if (ToolActions.AXE_STRIP.equals(toolAction)) {
			Block block = AxeItem.STRIPPABLES.get(state.getBlock());
			return block != null ? block.defaultBlockState() : null;
		}
		else if(ToolActions.AXE_SCRAPE.equals(toolAction)) return WeatheringCopper.getPrevious(state).orElse(null);
		else if(ToolActions.AXE_WAX_OFF.equals(toolAction)) return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((p_150694_) -> {
			return p_150694_.withPropertiesOf(state);
		}).orElse(null);
			//else if(ToolActions.HOE_TILL.equals(toolAction)) return HoeItem.getHoeTillingState(state); //TODO HoeItem bork
		else if (ToolActions.SHOVEL_FLATTEN.equals(toolAction)) return ShovelItem.FLATTENABLES.get(state.getBlock());
		return null;
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link Camera#getBlockAtCamera()}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 *
	 * @param state     the state
	 * @param level     the level
	 * @param pos       the position
	 * @param viewpoint the viewpoint
	 * @return the block state that should be 'seen'
	 */
	default BlockState getStateAtViewpoint(BlockState state, BlockGetter level, BlockPos pos, Vec3 viewpoint) {
		return state;
	}

	/**
	 * Called when a tree grows on top of this block and tries to set it to dirt by the trunk placer.
	 * An override that returns true is responsible for using the place function to
	 * set blocks in the world properly during generation. A modded grass block might override this method
	 * to ensure it turns into the corresponding modded dirt instead of regular dirt when a tree grows on it.
	 * For modded grass blocks, returning true from this method is NOT a substitute for adding your block
	 * to the #minecraft:dirt tag, rather for changing the behaviour to something other than setting to dirt.
	 *
	 * NOTE: This happens DURING world generation, the generation may be incomplete when this is called.
	 * Use the placeFunction when modifying the level.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param placeFunction Function to set blocks in the level for the tree, use this instead of the level directly
	 * @param randomSource The random source
	 * @param pos Position of the block to be set to dirt
	 * @param config Configuration of the trunk placer. Consider azalea trees, which should place rooted dirt instead of regular dirt.
	 * @return True to ignore vanilla behaviour
	 */
	default boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return false;
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 * <p>
	 * This method should only be used for blocks you don't control, for your own blocks override
	 * {@link Block#skipRendering(BlockState, BlockState, Direction)} on the respective block instead
	 * <p>
	 * WARNING: This method is likely to be called from a worker thread! If you want to retrieve a
	 *          {@link net.minecraft.world.level.block.entity.BlockEntity} from the given level, make sure to use
	 *          {@link net.minecraftforge.common.extensions.IForgeBlockGetter#getExistingBlockEntity(BlockPos)} to not
	 *          accidentally create a new or delete an old {@link net.minecraft.world.level.block.entity.BlockEntity}
	 *          off of the main thread as this would cause a write operation to the given {@link BlockGetter} and cause
	 *          a CME in the process. Any other direct or indirect write operation to the {@link BlockGetter} will have
	 *          the same outcome.
	 *
	 * @param level The world
	 * @param pos The blocks position in the world
	 * @param state The blocks {@link BlockState}
	 * @param neighborState The neighboring blocks {@link BlockState}
	 * @param dir The direction towards the neighboring block
	 */
	default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return false;
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link BlockStateExtensions#hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean supportsExternalFaceHiding(BlockState state) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ClientExtensionHooks.isBlockInSolidLayer(state);
		}
		return true;
	}
}
