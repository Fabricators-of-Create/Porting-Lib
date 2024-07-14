package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Fired when an Entity attempts to use a totem to prevent its death.
 *
 * <p>This event is {@linkplain CancellableEvent cancellable}.
 * If this event is cancelled, the totem will not prevent the entity's death.</p>
 *
 * <p>
 * This event is fired on the  {@linkplain EnvType#SERVER logical server}.
 * </p>
 */
public class LivingUseTotemEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onUseTotem(event);
	});

	private final DamageSource source;
	private final ItemStack totem;
	private final InteractionHand hand;

	public LivingUseTotemEvent(LivingEntity entity, DamageSource source, ItemStack totem, InteractionHand hand) {
		super(entity);
		this.source = source;
		this.totem = totem;
		this.hand = hand;
	}

	/**
	 * {@return the damage source that caused the entity to die}
	 */
	public DamageSource getSource() {
		return source;
	}

	/**
	 * {@return the totem of undying being used from the entity's inventory}
	 */
	public ItemStack getTotem() {
		return totem;
	}

	/**
	 * {@return the hand holding the totem}
	 */
	public InteractionHand getHandHolding() {
		return hand;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onUseTotem(this);
	}

	public interface Callback {
		void onUseTotem(LivingUseTotemEvent event);
	}
}
