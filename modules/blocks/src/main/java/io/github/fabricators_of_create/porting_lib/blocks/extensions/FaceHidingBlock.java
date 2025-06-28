package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import io.github.fabricators_of_create.porting_lib.blocks.ClientBlockHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface FaceHidingBlock {
	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link io.github.fabricators_of_create.porting_lib.blocks.injects.BlockStateInjection#port_lib$hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean supportsExternalFaceHiding(BlockState state) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ClientBlockHooks.isBlockInSolidLayer(state);
		}
		return true;
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
}
