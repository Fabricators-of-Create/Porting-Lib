package io.github.fabricators_of_create.porting_lib.level.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

/**
 * This event is fired when all players are asleep and the time should be set to day.<br>
 * <p>
 * setTimeAddition(wakeUpTime) sets a new time that will be added to the dayTime.<br>
 */
public class SleepFinishedTimeEvent extends LevelEvent {
	public static final Event<SleepFinishedCallback> SLEEP_FINISHED = EventFactory.createArrayBacked(SleepFinishedCallback.class, callbacks -> event -> {
		for (SleepFinishedCallback e : callbacks)
			e.onSleepFinished(event);
	});
	private long newTime;
	private final long minTime;

	public SleepFinishedTimeEvent(ServerLevel level, long newTime, long minTime) {
		super(level);
		this.newTime = newTime;
		this.minTime = minTime;
	}

	/**
	 * @return the new time
	 */
	public long getNewTime() {
		return newTime;
	}

	/**
	 * Sets the new time which should be set when all players wake up
	 *
	 * @param newTimeIn The new time at wakeup
	 * @return {@code false} if newTimeIn was lower than current time
	 */
	public boolean setTimeAddition(long newTimeIn) {
		if (minTime > newTimeIn)
			return false;
		this.newTime = newTimeIn;
		return true;
	}

	@Override
	public void sendEvent() {
		SLEEP_FINISHED.invoker().onSleepFinished(this);
	}

	@FunctionalInterface
	public interface SleepFinishedCallback {
		void onSleepFinished(SleepFinishedTimeEvent event);
	}
}
