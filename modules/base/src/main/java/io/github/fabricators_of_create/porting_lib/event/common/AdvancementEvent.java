package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;

/**
 * Base class used for advancement-related events. Should not be used directly.
 *
 * @see AdvancementEarnEvent
 * @see AdvancementProgressEvent
 */
public abstract class AdvancementEvent extends PlayerEvents {
	public static final Event<EarnCallback> EARN = EventFactory.createArrayBacked(EarnCallback.class, callbacks -> event -> {
		for (EarnCallback c : callbacks)
			c.onAdvancementEarn(event);
	});
	public static final Event<ProgressCallback> PROGRESS = EventFactory.createArrayBacked(ProgressCallback.class, callbacks -> event -> {
		for (ProgressCallback c : callbacks)
			c.onAdvancementProgress(event);
	});
	private final AdvancementHolder advancement;

	public AdvancementEvent(Player player, AdvancementHolder advancement) {
		super(player);
		this.advancement = advancement;
	}

	public AdvancementHolder getAdvancement() {
		return advancement;
	}

	/**
	 * Fired when the player earns an advancement. An advancement is earned once its requirements are complete.
	 *
	 * <p>Note that advancements may be hidden from the player or used in background mechanics, such as recipe
	 * advancements for unlocking recipes in the recipe book.</p>
	 *
	 * @see AdvancementProgress#isDone()
	 */
	public static class AdvancementEarnEvent extends AdvancementEvent {
		public AdvancementEarnEvent(Player player, AdvancementHolder earned) {
			super(player, earned);
		}

		@Override
		public void sendEvent() {
			EARN.invoker().onAdvancementEarn(this);
		}
	}

	/**
	 * Fired when the player's progress on an advancement criterion is granted or revoked.
	 *
	 * <p>This event is not cancellable, and does not have a result.</p>
	 *
	 * @see AdvancementEarnEvent
	 * @see net.minecraft.server.PlayerAdvancements#award(AdvancementHolder, String)
	 * @see net.minecraft.server.PlayerAdvancements#revoke(AdvancementHolder, String)
	 */
	public static class AdvancementProgressEvent extends AdvancementEvent {
		private final AdvancementProgress advancementProgress;
		private final String criterionName;
		private final AdvancementEvent.AdvancementProgressEvent.ProgressType progressType;

		public AdvancementProgressEvent(Player player, AdvancementHolder progressed, AdvancementProgress advancementProgress, String criterionName, AdvancementEvent.AdvancementProgressEvent.ProgressType progressType) {
			super(player, progressed);
			this.advancementProgress = advancementProgress;
			this.criterionName = criterionName;
			this.progressType = progressType;
		}

		/**
		 * {@return the progress of the advancement}
		 */
		public AdvancementProgress getAdvancementProgress() {
			return advancementProgress;
		}

		/**
		 * {@return name of the criterion that was progressed}
		 */
		public String getCriterionName() {
			return criterionName;
		}

		/**
		 * {@return The type of progress for the criterion in this event}
		 */
		public ProgressType getProgressType() {
			return progressType;
		}

		@Override
		public void sendEvent() {
			PROGRESS.invoker().onAdvancementProgress(this);
		}

		public enum ProgressType {
			GRANT, REVOKE
		}
	}

	@FunctionalInterface
	public interface EarnCallback {
		void onAdvancementEarn(AdvancementEarnEvent event);
	}

	@FunctionalInterface
	public interface ProgressCallback {
		void onAdvancementProgress(AdvancementProgressEvent event);
	}
}
