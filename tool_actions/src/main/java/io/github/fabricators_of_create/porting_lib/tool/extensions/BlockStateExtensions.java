package io.github.fabricators_of_create.porting_lib.tool.extensions;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
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
}
