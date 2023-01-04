package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PlayerEvents extends EntityEvent {

	public static final Event<PlayerBreakSpeed> BREAK_SPEED = EventFactory.createArrayBacked(PlayerBreakSpeed.class, callbacks -> event -> {
		for(PlayerBreakSpeed e : callbacks)
			e.setBreakSpeed(event);
	});

	public static final Event<PlayerXpPickUp> PICKUP_XP = EventFactory.createArrayBacked(PlayerXpPickUp.class, callbacks -> event -> {
		for (PlayerXpPickUp e : callbacks)
			e.onPlayerPicksUpXp(event);
	});

	public static final Event<PlayerXpChange> XP_CHANGE = EventFactory.createArrayBacked(PlayerXpChange.class, callbacks -> event -> {
		for (PlayerXpChange e : callbacks)
			e.onPlayerXpChange(event);
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

	public static class PickupXp extends PlayerEvents {

		private final ExperienceOrb orb;

		public PickupXp(Player player, ExperienceOrb orb) {
			super(player);
			this.orb = orb;
		}

		public ExperienceOrb getOrb() {
			return orb;
		}

		@Override
		public void sendEvent() {
			PICKUP_XP.invoker().onPlayerPicksUpXp(this);
		}
	}

	public static class XpChange extends PlayerEvents {
		private int amount;

		public XpChange(Player player, int amount) {
			super(player);
			this.amount = amount;
		}

		public int getAmount() {
			return this.amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		@Override
		public void sendEvent() {
			XP_CHANGE.invoker().onPlayerXpChange(this);
		}
	}

	public static class BreakSpeed extends PlayerEvents {
		private final BlockState state;
		private final float originalSpeed;
		private float newSpeed = 0.0f;
		private final BlockPos pos; // Y position of -1 notes unknown location

		public BreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
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
	public interface PlayerXpChange {
		void onPlayerXpChange(XpChange event);
	}

	@FunctionalInterface
	public interface PlayerXpPickUp {
		void onPlayerPicksUpXp(PickupXp event);
	}
}
