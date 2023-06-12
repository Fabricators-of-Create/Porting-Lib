package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import io.github.fabricators_of_create.porting_lib.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.common.util.PlantType;
import io.github.fabricators_of_create.porting_lib.extensions.ClientExtensionHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.phys.Vec3;

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
