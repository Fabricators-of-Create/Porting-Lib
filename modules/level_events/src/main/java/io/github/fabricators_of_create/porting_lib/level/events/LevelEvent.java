package io.github.fabricators_of_create.porting_lib.level.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.minecraft.world.level.LevelAccessor;

/**
 * This event is fired whenever an event involving a {@link LevelAccessor} occurs.
 */
public abstract class LevelEvent extends BaseEvent {
	private final LevelAccessor level;

	public LevelEvent(LevelAccessor level)
	{
		this.level = level;
	}

	/**
	 * {@return the level this event is affecting}
	 */
	public LevelAccessor getLevel()
	{
		return level;
	}
}
