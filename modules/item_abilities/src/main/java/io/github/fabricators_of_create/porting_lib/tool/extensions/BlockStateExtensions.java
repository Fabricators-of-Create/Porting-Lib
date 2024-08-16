package io.github.fabricators_of_create.porting_lib.tool.extensions;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilityHooks;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityBlock;
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
	 * For example: Used to determine if {@link ItemAbilities#AXE_STRIP an axe can strip},
	 * {@link ItemAbilities#SHOVEL_FLATTEN a shovel can path}, or {@link ItemAbilities#HOE_TILL a hoe can till}.
	 * Returns {@code null} if nothing should happen.
	 *
	 * @param context     The use on context that the action was performed in
	 * @param itemAbility The action being performed by the tool
	 * @param simulate    If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		var blockState = (BlockState) this;
		BlockState eventState = ItemAbilityHooks.onToolUse(blockState, context, itemAbility, simulate);
		return eventState != blockState ? eventState : (blockState.getBlock() instanceof ItemAbilityBlock abilityBlock ? abilityBlock.getToolModifiedState(blockState, context, itemAbility, simulate) : null);
	}
}
