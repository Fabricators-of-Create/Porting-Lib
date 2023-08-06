package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public abstract class BlockEvents {
	/**
	 * Modify the amount of experience dropped by a block.
	 */
	public static final Event<ModifyExp> MODIFY_EXP = EventFactory.createArrayBacked(ModifyExp.class, callbacks -> (level, state, pos, player, amount) -> {
		for (ModifyExp callback : callbacks)
			amount = callback.modifyExp(level, state, pos, player, amount);
		return amount;
	});

	@FunctionalInterface
	public interface ModifyExp {
		/**
		 * @param player the player who broke the block, or null if not present
		 * @return the modified amount, or the original if unchanged. return <= 0 to cancel.
		 */
		int modifyExp(ServerLevel level, BlockState state, BlockPos pos, @Nullable Player player, int amount);
	}

	/**
	 * Invoked before a block is placed from the head of {@link BlockItem#useOn(UseOnContext)}. Called on both client and server.
	 * Return null to fall back to further processing. Any non-null value will result in placement being cancelled.
	 */
	public static final Event<BeforePlace> BEFORE_PLACE = EventFactory.createArrayBacked(BeforePlace.class, callbacks -> context -> {
		for (BeforePlace callback : callbacks) {
			InteractionResult result = callback.beforePlace(context);
			if (result != null)
				return result;
		}
//		return InteractionResult.PASS;
		//noinspection removal
		InteractionResult legacyResult = BlockPlaceCallback.EVENT.invoker().onBlockPlace(context);
		return legacyResult == InteractionResult.PASS ? null : legacyResult;
	});

	public interface BeforePlace {
		@Nullable
		InteractionResult beforePlace(BlockPlaceContext ctx);
	}

	/**
	 * Invoked after a block is placed, from {@link BlockItem#useOn(UseOnContext)}. Called on both client and server.
	 */
	public static final Event<AfterPlace> AFTER_PLACE = EventFactory.createArrayBacked(AfterPlace.class, callbacks -> context -> {
		for (AfterPlace callback : callbacks)
			callback.afterPlace(context);
	});

	public interface AfterPlace {
		void afterPlace(BlockPlaceContext ctx);
	}
}
