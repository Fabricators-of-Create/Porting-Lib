package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface PlayerBreakSpeedCallback {
	Event<PlayerBreakSpeedCallback> EVENT = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class, callbacks -> breakSpeed -> {
		for (PlayerBreakSpeedCallback event : callbacks)
			event.setBreakSpeed(breakSpeed);
	});

	void setBreakSpeed(BreakSpeed event);

	final class BreakSpeed {
		public final PlayerEntity player;
		public final BlockState state;
		public final float originalSpeed;
		@Nullable
		public final BlockPos pos;
		public float newSpeed;

		public BreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos) {
			this.player = player;
			this.state = state;
			this.originalSpeed = original;
			this.newSpeed = original;
			this.pos = pos;
		}
	}
}

