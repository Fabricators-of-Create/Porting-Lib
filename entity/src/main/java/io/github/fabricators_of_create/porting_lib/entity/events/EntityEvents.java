package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerExperienceEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

/**
 * Miscellaneous entity events that don't fit into subcategories.
 * @see AdditionalEntityTrackingEvents
 * @see EntityDataEvents
 * @see EntityMountEvents
 * @see EntityMoveEvents
 * @see LightningStrikeEvents
 *
 * @see LivingEntityEvents
 * @see LivingEntityDamageEvents
 * @see LivingEntityLootEvents
 *
 * @see PlayerEvents
 * @see PlayerExperienceEvents
 * @see PlayerTickEvents
 */
public class EntityEvents {
	/**
	 * Fired when an entity's size changes. Allows for modification of dimensions and eye height.
	 * Cancellation will stop later listeners from modifying values.
	 */
	public static final Event<Size> SIZE = CancelBypass.makeEvent(Size.class, holder -> callbacks -> event -> {
		for (Size callback : callbacks) {
			if (event.shouldInvokeListener(holder, callback))
				callback.modifySize(event);
		}
	});

	@FunctionalInterface
	public interface Size {
		void modifySize(EntitySizeEvent event);
	}

	public static class EntitySizeEvent extends CancellableEvent {
		public final Entity entity;
		public final Pose pose;
		public final float originalEyeHeight;
		public final EntityDimensions originalDimensions;

		public float eyeHeight;
		public EntityDimensions dimensions;

		public EntitySizeEvent(Entity entity, Pose pose, float height, EntityDimensions dimensions) {
			this(entity, pose, height, height, dimensions, dimensions);
		}

		public EntitySizeEvent(Entity entity, Pose pose, float oldHeight, float newHeight, EntityDimensions oldDimensions, EntityDimensions newDimensions) {
			this.entity = entity;
			this.pose = pose;
			this.originalEyeHeight = oldHeight;
			this.eyeHeight = newHeight;
			this.originalDimensions = oldDimensions;
			this.dimensions = newDimensions;
		}
	}

	/**
	 * This event is fired when an attack is blocked with a shield. Cancelling it will prevent blocking.
	 * Listeners have the ability to change the amount of damage blocked and determine whether the shield
	 * item gets damaged or not.
	 */
	public static final Event<ShieldBlock> SHIELD_BLOCK = CancelBypass.makeEvent(ShieldBlock.class, holder -> callbacks -> event -> {
		for (ShieldBlock callback : callbacks) {
			if (event.shouldInvokeListener(holder, callback))
				callback.onShieldBlock(event);
		}
	});

	@FunctionalInterface
	public interface ShieldBlock {
		void onShieldBlock(ShieldBlockEvent event);
	}

	public static class ShieldBlockEvent extends CancellableEvent {
		public final LivingEntity blocker;
		public final ItemStack shield;
		public final DamageSource source;

		public float damageBlocked;
		public boolean damageShield = true;

		public ShieldBlockEvent(LivingEntity blocker, ItemStack shield, DamageSource source, float damageBlocked) {
			this.blocker = blocker;
			this.shield = shield;
			this.source = source;
			this.damageBlocked = damageBlocked;
		}
	}

	/**
	 * Fired when a projectile collides with either a block or an entity.
	 */
	public static final Event<ProjectileImpact> PROJECTILE_IMPACT = EventFactory.createArrayBacked(ProjectileImpact.class, callbacks -> (proj, hit) -> {
		for (ProjectileImpact callback : callbacks) {
			if (callback.onImpact(proj, hit)) return true;
		}
		return false;
	});

	@FunctionalInterface
	public interface ProjectileImpact {
		/**
		 * @return true to cancel vanilla hit logic
		 */
		boolean onImpact(Projectile projectile, HitResult hitResult);
	}
}
