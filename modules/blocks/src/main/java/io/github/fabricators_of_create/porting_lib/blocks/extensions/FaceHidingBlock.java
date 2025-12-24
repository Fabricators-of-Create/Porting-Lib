package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import io.github.fabricators_of_create.porting_lib.blocks.injects.BlockStateInjection;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

//  This isn't feasible to support anymore in 1.21.11
//public interface FaceHidingBlock {
//	/**
//	 * Whether this block hides the neighbors face pointed towards by the given direction.
//	 * <p>
//	 * This method should only be used for blocks you don't control, for your own blocks override
//	 * {@link Block#skipRendering(BlockState, BlockState, Direction)} on the respective block instead
//	 * <p>
//	 * <b>Note that this method may be called on any of the client's meshing threads.</b><br/>
//	 * As such, if you need any data from your {@link BlockEntity}, you should put it in a render data object to guarantee
//	 * safe concurrent access to it on the client.<br/>
//	 * {@link FabricBlockView#getBlockEntityRenderData(BlockPos)} will return the render data for the queried block,
//	 * or null if none is present.
//	 *
//	 * @param level         The world
//	 * @param pos           The blocks position in the world
//	 * @param state         The blocks {@link BlockState}
//	 * @param neighborState The neighboring blocks {@link BlockState}
//	 * @param dir           The direction towards the neighboring block
//	 */
//	default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
//		return false;
//	}
//
//	/**
//	 * Whether this block allows a neighboring block to hide the face of this block it touches.
//	 * If this returns true, {@link BlockStateInjection#port_lib$hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
//	 * will be called on the neighboring block.
//	 */
//	default boolean supportsExternalFaceHiding(BlockState state) {
//		return true;
//	}
//}
