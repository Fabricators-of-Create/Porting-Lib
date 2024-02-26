package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

/**
 * Will be removed in 1.20.2+
 * Use {@link ProjectileImpactEvent} instead.
 */
@Deprecated(forRemoval = true)
public interface ProjectileImpactCallback {
	Event<ProjectileImpactCallback> EVENT = EventFactory.createArrayBacked(ProjectileImpactCallback.class, callbacks -> (proj, hit) -> {
		for (ProjectileImpactCallback callback : callbacks) {
			if (callback.onImpact(proj, hit)) return true;
		}
		return false;
	});

	boolean onImpact(Projectile projectile, HitResult hitResult);
}
