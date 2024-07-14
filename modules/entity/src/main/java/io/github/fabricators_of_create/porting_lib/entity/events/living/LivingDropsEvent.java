package io.github.fabricators_of_create.porting_lib.entity.events.living;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * LivingDropsEvent is fired when an Entity's death causes dropped items to appear.<br>
 * This event is fired whenever an Entity dies and drops items in
 * {@link LivingEntity#die(DamageSource)}.<br>
 * <br>
 * This event is fired via the {@link EntityHooks#onLivingDrops(LivingEntity, DamageSource, Collection, boolean)} .<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the drop to occur.<br>
 * {@link #drops} contains the ArrayList of EntityItems that will be dropped.<br>
 * {@link #recentlyHit} determines whether the Entity doing the drop has recently been damaged.<br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the Entity does not drop anything.<br>
 **/
public class LivingDropsEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onLivingDrop(event);
	});

	private final DamageSource source;
	private final Collection<ItemEntity> drops;
	private final boolean recentlyHit;

	public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit) {
		super(entity);
		this.source = source;
		this.drops = drops;
		this.recentlyHit = recentlyHit;
	}

	public DamageSource getSource() {
		return source;
	}

	public Collection<ItemEntity> getDrops() {
		return drops;
	}

	public boolean isRecentlyHit() {
		return recentlyHit;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onLivingDrop(this);
	}

	public interface Callback {
		void onLivingDrop(LivingDropsEvent event);
	}
}
