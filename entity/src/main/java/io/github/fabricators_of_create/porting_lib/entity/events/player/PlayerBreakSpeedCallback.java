package io.github.fabricators_of_create.porting_lib.entity.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface PlayerBreakSpeedCallback {
	/**
	 * Fired while a player breaks a block. Modifies the result of {@link Player#getDestroySpeed(BlockState)}.
	 * This event is chained; multiple listeners may modify the speed.
	 */
	Event<PlayerBreakSpeedCallback> EVENT = EventFactory.createArrayBacked(PlayerBreakSpeedCallback.class, callbacks -> (player, state, pos, speed) -> {
		for(PlayerBreakSpeedCallback e : callbacks)
			speed = e.modifyBreakSpeed(player, state, pos, speed);
		return speed;
	});

	/**
	 * @return the modified break speed, or the original if unchanged
	 */
	float modifyBreakSpeed(Player player, BlockState state, BlockPos pos, float speed);
}
