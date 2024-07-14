package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Event for when an entity drops experience on its death, can be used to change
 * the amount of experience points dropped or completely prevent dropping of experience
 * by canceling the event.
 */
public class LivingExperienceDropEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onDropExperience(event);
	});

	@Nullable
	private final Player attackingPlayer;
	private final int originalExperiencePoints;

	private int droppedExperiencePoints;

	public LivingExperienceDropEvent(LivingEntity entity, @Nullable Player attackingPlayer, int originalExperience) {
		super(entity);

		this.attackingPlayer = attackingPlayer;
		this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
	}

	public int getDroppedExperience() {
		return droppedExperiencePoints;
	}

	public void setDroppedExperience(int droppedExperience) {
		this.droppedExperiencePoints = droppedExperience;
	}

	/**
	 * @return The player that last attacked the entity and thus caused the experience. This can be null, in case the player has since logged out.
	 */
	@Nullable
	public Player getAttackingPlayer() {
		return attackingPlayer;
	}

	public int getOriginalExperience() {
		return originalExperiencePoints;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onDropExperience(this);
	}

	public interface Callback {
		void onDropExperience(LivingExperienceDropEvent event);
	}
}
