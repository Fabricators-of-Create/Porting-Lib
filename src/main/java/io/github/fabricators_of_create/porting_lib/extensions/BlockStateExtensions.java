package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;


public interface BlockStateExtensions {
	/**
	 * Returns the state that this block should transform into when right-clicked by a tool.
	 * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
	 * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
	 * Returns {@code null} if nothing should happen.
	 *
	 * @param context The use on context that the action was performed in
	 * @param toolAction The action being performed by the tool
	 * @param simulate If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(UseOnContext context, ToolAction toolAction, boolean simulate) {
		return  ((BlockState) this).getBlock().getToolModifiedState(((BlockState) this), context, toolAction, simulate);
	}

	/**
	 * Returns the state that this block should transform into when right clicked by a tool.
	 * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
	 * Return null if vanilla behavior should be disabled.
	 *
	 * @param world The world
	 * @param pos The block position in world
	 * @param player The player clicking the block
	 * @param stack The stack being used by the player
	 * @param toolAction The tool type to be considered when performing the action
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
		return ((BlockState) this).getBlock().getToolModifiedState(((BlockState) this), world, pos, player, stack, toolAction);
	}

	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow.
	 * Some examples:
	 *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
	 *   Cacti checks if its a cacti, or if its sand
	 *   Nether types check for soul sand
	 *   Crops check for tilled soil
	 *   Caves check if it's a solid surface
	 *   Plains check if its grass or dirt
	 *   Water check if its still water
	 *
	 * @param level The current level
	 * @param facing The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	default boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return ((BlockState)this).getBlock().canSustainPlant(((BlockState)this), level, pos, facing, plantable);
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 * <p>
	 * This method should only be used for blocks you don't control, for your own blocks override
	 * {@link net.minecraft.world.level.block.Block#skipRendering(BlockState, BlockState, Direction)}
	 * on the respective block instead
	 *
	 * @param level The world
	 * @param pos The blocks position in the world
	 * @param neighborState The neighboring blocks {@link BlockState}
	 * @param dir The direction towards the neighboring block
	 */
	default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
		return ((BlockState)this).getBlock().hidesNeighborFace(level, pos, ((BlockState)this), neighborState, dir);
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link BlockStateExtensions#hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean supportsExternalFaceHiding() {
		return ((BlockState)this).getBlock().supportsExternalFaceHiding(((BlockState)this));
	}
}
