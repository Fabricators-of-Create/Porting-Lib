package io.github.fabricators_of_create.porting_lib.entity.events.player;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

/**
 * PlayerXpEvent is fired whenever an event involving player experience occurs. <br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 */
public abstract class PlayerXpEvent extends PlayerEvent {
	public PlayerXpEvent(Player player) {
		super(player);
	}

	/**
	 * This event is fired after the player collides with an experience orb, but before the player has been given the experience.
	 * It can be cancelled, and no further processing will be done.
	 */
	public static class PickupXp extends PlayerXpEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPickupXp(event);
		});

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
			EVENT.invoker().onPickupXp(this);
		}

		public interface Callback {
			void onPickupXp(PickupXp event);
		}
	}

	/**
	 * This event is fired when the player's experience changes through the {@link Player#giveExperiencePoints(int)} method.
	 * It can be cancelled, and no further processing will be done.
	 */
	public static class XpChange extends PlayerXpEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onXpChange(event);
		});

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
			EVENT.invoker().onXpChange(this);
		}

		public interface Callback {
			void onXpChange(XpChange event);
		}
	}

	/**
	 * This event is fired when the player's experience level changes through the {@link Player#giveExperienceLevels(int)} method.
	 * It can be cancelled, and no further processing will be done.
	 */
	public static class LevelChange extends PlayerXpEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onLevelChange(event);
		});

		private int levels;

		public LevelChange(Player player, int levels) {
			super(player);
			this.levels = levels;
		}

		public int getLevels() {
			return this.levels;
		}

		public void setLevels(int levels) {
			this.levels = levels;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLevelChange(this);
		}

		public interface Callback {
			void onLevelChange(LevelChange event);
		}
	}
}
