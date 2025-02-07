package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;

public interface ProjectileImpactCallback {
	Event<ProjectileImpactCallback> EVENT = EventFactory.createArrayBacked(ProjectileImpactCallback.class, callbacks -> (proj, hit) -> {
		for (ProjectileImpactCallback callback : callbacks) {
			if (callback.onImpact(proj, hit)) return true;
		}
		return false;
	});

	boolean onImpact(ProjectileEntity projectile, HitResult hitResult);
}
