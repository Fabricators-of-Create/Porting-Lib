package io.github.fabricators_of_create.porting_lib.entity.events;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

	private ImpactResult result = ImpactResult.DEFAULT;

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

	/**
	 * This method only exists in lex forge and is here for compatibility
	 */
	@Deprecated()
	public void setImpactResult(ImpactResult result) {
		this.result = Objects.requireNonNull(result);
	}

	/**
	 * This method only exists in lex forge and is here for compatibility
	 */
	@Deprecated()
	public ImpactResult getImpactResult() {
		return this.isCanceled() ? ImpactResult.SKIP_ENTITY : result;
	}

	@Override
	public void sendEvent() {
		PROJECTILE_IMPACT.invoker().onProjectileImpact(this);
	}

	public enum ImpactResult {
		/**
		 * The default behaviour, the projectile will be destroyed and the hit will be processed.
		 */
		DEFAULT,
		/**
		 * The projectile will pass through the current entity as if it wasn't there. This will return default behaviour if there is no entity.
		 */
		SKIP_ENTITY,
		/**
		 * Damage the entity and stop the projectile here, the projectile will not pierce.
		 */
		STOP_AT_CURRENT,
		/**
		 * Cancel the piercing aspect of the projectile, and do not damage the entity.
		 */
		STOP_AT_CURRENT_NO_DAMAGE
	}
}
