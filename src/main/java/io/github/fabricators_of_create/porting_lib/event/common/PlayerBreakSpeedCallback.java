package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public interface PlayerBreakSpeedCallback {
	Event<PlayerBreakSpeedCallback> EVENT = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class, callbacks -> breakSpeed -> {
		for (PlayerBreakSpeedCallback event : callbacks)
			event.setBreakSpeed(breakSpeed);
	});

	void setBreakSpeed(BreakSpeed event);

	final class BreakSpeed {
		public final Player player;
		public final BlockState state;
		public final float originalSpeed;
		@Nullable
		public final BlockPos pos;
		public float newSpeed;

		public BreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
			this.player = player;
			this.state = state;
			this.originalSpeed = original;
			this.newSpeed = original;
			this.pos = pos;
		}
	}
}

