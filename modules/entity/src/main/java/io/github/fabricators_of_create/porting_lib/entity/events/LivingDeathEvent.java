package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Note this event is fired after fabrics {@link net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents#ALLOW_DEATH}.
 * You should generally try to use fabric's event
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in
 * {@link LivingEntity#die(DamageSource)},
 * {@link Player#die(DamageSource)}, and
 * {@link ServerPlayer#die(DamageSource)}. <br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is cancelable.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result.<br>
 * <br>
 **/
@Deprecated
public class LivingDeathEvent extends LivingEntityEvents {
	public static final Event<Death> DEATH = EventFactory.createArrayBacked(Death.class, callbacks -> event -> {
		for (Death e : callbacks)
			e.onLivingDeath(event);
	});
	private final DamageSource source;

	public LivingDeathEvent(LivingEntity entity, DamageSource source) {
		super(entity);
		this.source = source;
	}

	public DamageSource getSource() {
		return source;
	}

	@Override
	public void sendEvent() {
		DEATH.invoker().onLivingDeath(this);
	}

	@FunctionalInterface
	public interface Death {
		void onLivingDeath(LivingDeathEvent event);
	}
}
