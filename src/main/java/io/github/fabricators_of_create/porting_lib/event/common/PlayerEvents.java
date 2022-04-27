package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.event.EntityEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PlayerEvents extends EntityEvent {

	public static Event<PlayerBreakSpeed> BREAK_SPEED = EventFactory.createArrayBacked(PlayerBreakSpeed.class, callbacks -> event -> {
		for(PlayerBreakSpeed e : callbacks)
			e.setBreakSpeed(event);
	});

	public static Event<PlayerChangedDimensionEvent> CHANGED_DIMENSION = EventFactory.createArrayBacked(PlayerChangedDimensionEvent.class, callbacks -> (player, fromDim, toDim) -> {
		for(PlayerChangedDimensionEvent e : callbacks)
			e.onChangedDimension(player, fromDim, toDim);
	});

	private final Player entityPlayer;

	public PlayerEvents(Player player) {
		super(player);
		entityPlayer = player;
	}

	/**
	 * @return Player
	 */
	public Player getPlayer() {
		return entityPlayer;
	}

	public static class BreakSpeed extends PlayerEvents
	{
		private final BlockState state;
		private final float originalSpeed;
		private float newSpeed = 0.0f;
		private final BlockPos pos; // Y position of -1 notes unknown location

		public BreakSpeed(Player player, BlockState state, float original, BlockPos pos)
		{
			super(player);
			this.state = state;
			this.originalSpeed = original;
			this.setNewSpeed(original);
			this.pos = pos != null ? pos : new BlockPos(0, -1, 0);
		}

		public BlockState getState() { return state; }
		public float getOriginalSpeed() { return originalSpeed; }
		public float getNewSpeed() { return newSpeed; }
		public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
		public BlockPos getPos() { return pos; }

		@Override
		public void sendEvent() {
			BREAK_SPEED.invoker().setBreakSpeed(this);
		}
	}

	@FunctionalInterface
	public interface PlayerBreakSpeed {
		void setBreakSpeed(BreakSpeed event);
	}

	@FunctionalInterface
	public interface PlayerChangedDimensionEvent {
		void onChangedDimension(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim);
	}
}
