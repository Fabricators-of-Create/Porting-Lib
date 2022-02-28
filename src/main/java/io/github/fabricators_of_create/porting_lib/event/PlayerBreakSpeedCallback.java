package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface PlayerBreakSpeedCallback {
	Event<PlayerBreakSpeedCallback> EVENT = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class, callbacks -> breakSpeed -> {
		for (PlayerBreakSpeedCallback event : callbacks)
			event.setBreakSpeed(breakSpeed);
	});

	void setBreakSpeed(BreakSpeed event);

	class BreakSpeed {
		private final Player player;
		private final BlockState state;
		private final float originalSpeed;
		private final BlockPos pos; // Y position of -1 notes unknown location
		private float newSpeed = 0.0f;

		public BreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
			this.player = player;
			this.state = state;
			this.originalSpeed = original;
			this.newSpeed = original;
			this.pos = pos != null ? pos : new BlockPos(0, -1, 0);
		}

		public Player getPlayer() {
			return player;
		}

		public BlockState getState() {
			return state;
		}

		public float getOriginalSpeed() {
			return originalSpeed;
		}

		public float getNewSpeed() {
			return newSpeed;
		}

		public void setNewSpeed(float newSpeed) {
			this.newSpeed = newSpeed;
		}

		public BlockPos getPos() {
			return pos;
		}
	}
}

