package io.github.fabricators_of_create.porting_lib.tool;

import io.github.fabricators_of_create.porting_lib.tool.events.BlockToolModificationEvent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class ItemAbilityHooks {
	@Nullable
	public static BlockState onToolUse(BlockState originalState, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		BlockToolModificationEvent event = new BlockToolModificationEvent(originalState, context, itemAbility, simulate);
		return event.post() ? null : event.getFinalState();
	}
}
