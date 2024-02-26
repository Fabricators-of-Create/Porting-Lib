package io.github.fabricators_of_create.porting_lib.entity.events;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

/**
 * This event is fired when a projectile entity impacts something.<br>
 * This event is fired for all vanilla projectiles by Porting Lib,
 * custom projectiles should fire this event and check the result in a similar fashion.
 * This event is cancelable. When canceled, the impact will not be processed and the projectile will continue flying.
 * Killing or other handling of the entity after event cancellation is up to the modder.
 */
public class ProjectileImpactEvent extends EntityEvents {
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
//		setCanceled(ProjectileImpactCallback.EVENT.invoker().onImpact(getProjectile(), getRayTraceResult())); ProjectileImpactCallback fires for all projectiles including none vanilla
		PROJECTILE_IMPACT.invoker().onProjectileImpact(this);
	}
}
