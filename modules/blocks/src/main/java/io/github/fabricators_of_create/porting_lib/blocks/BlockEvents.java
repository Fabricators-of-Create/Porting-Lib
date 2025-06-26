package io.github.fabricators_of_create.porting_lib.blocks;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.player.PlayerEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockEvents {
	private BlockEvents() {}

	/**
	 * HarvestCheck is fired when a player attempts to harvest a block.<br>
	 * This event is fired whenever a player attempts to harvest a block in
	 * {@link Player#hasCorrectToolForDrops(BlockState)}.<br>
	 * <br>
	 * This event is fired via the {@link BlockHooks#doPlayerHarvestCheck(Player, BlockState, BlockGetter, BlockPos)}.<br>
	 * <br>
	 * {@link #state} contains the {@link BlockState} that is being checked for harvesting. <br>
	 * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
	 * <br>
	 * This event is not {@link CancellableEvent}.<br>
	 * <br>
	 **/
	public static class HarvestCheck extends PlayerEvent {
		public static final Event<HarvestCheckCallback> EVENT = EventFactory.createArrayBacked(HarvestCheckCallback.class, callbacks -> evnet -> {
			for (HarvestCheckCallback callback : callbacks)
				callback.onHarvest(evnet);
		});

		private final BlockState state;
		private final BlockGetter level;
		private final BlockPos pos;
		private boolean success;

		public HarvestCheck(Player player, BlockState state, BlockGetter level, BlockPos pos, boolean success) {
			super(player);
			this.state = state;
			this.level = level;
			this.pos = pos;
			this.success = success;
		}

		public BlockState getTargetBlock() {
			return this.state;
		}

		public BlockGetter getLevel() {
			return level;
		}

		public BlockPos getPos() {
			return pos;
		}

		public boolean canHarvest() {
			return this.success;
		}

		public void setCanHarvest(boolean success) {
			this.success = success;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onHarvest(this);
		}
	}

	public interface HarvestCheckCallback {
		void onHarvest(HarvestCheck event);
	}
}
