package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

/**
 * This event is fired when a projectile entity impacts something.<br>
 * This event is fired via {@link EntityHooks#onProjectileImpact(Projectile, HitResult)}
 * This event is fired for all vanilla projectiles by Porting Lib,
 * custom projectiles should fire this event and check the result in a similar fashion.
 * This event is cancelable. When canceled, the impact will not be processed and the projectile will continue flying.
 * Killing or other handling of the entity after event cancellation is up to the modder.
 */
public class ProjectileImpactEvent extends EntityEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onImpact(event);
	});

	private final HitResult ray;
	private final Projectile projectile;

	public ProjectileImpactEvent(Projectile projectile, HitResult ray) {
		super(projectile);
		this.ray = ray;
		this.projectile = projectile;
	}

	public HitResult getRayTraceResult() {
		return ray;
	}

	public Projectile getProjectile() {
		return projectile;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onImpact(this);
	}

	public interface Callback {
		void onImpact(ProjectileImpactEvent event);
	}
}
