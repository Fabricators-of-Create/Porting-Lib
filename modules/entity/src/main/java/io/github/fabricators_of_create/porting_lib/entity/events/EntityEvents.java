package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class EntityEvents extends BaseEvent {
	protected final Entity entity;

	public EntityEvents(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public static final Event<EntitySize> SIZE = EventFactory.createArrayBacked(EntitySize.class, callbacks -> event -> {
		for (EntitySize callback : callbacks)
			callback.onEntitySizeChange(event);
	});

	public static final Event<JoinWorld> ON_JOIN_WORLD = EventFactory.createArrayBacked(JoinWorld.class, callbacks -> (entity, world, loadedFromDisk) -> {
		for (JoinWorld callback : callbacks)
			if (!callback.onJoinWorld(entity, world, loadedFromDisk))
				return true;
		return false;
	});

	public static final Event<Remove> ON_REMOVE = EventFactory.createArrayBacked(Remove.class, callbacks -> ((entity, reason) -> {
		for (Remove e : callbacks)
			e.onRemove(entity, reason);
	}));

	public static final Event<Teleport> TELEPORT = EventFactory.createArrayBacked(Teleport.class, callbacks -> (event) -> {
		for (Teleport callback : callbacks) {
			callback.onTeleport(event);
			if (event.isCanceled())
				return;
		}
	});

	public static final Event<Tracking> START_TRACKING_TAIL = EventFactory.createArrayBacked(Tracking.class, callbacks -> (entity, player) -> {
		for (Tracking callback : callbacks) {
			callback.onTrackingStart(entity, player);
		}
	});

	public static final Event<EnteringSection> ENTERING_SECTION = EventFactory.createArrayBacked(EnteringSection.class, callbacks -> (entity, packedOldPos, packedNewPos) -> {
		for (EnteringSection e : callbacks)
			e.onEntityEnterSection(entity, packedOldPos, packedNewPos);
	});

	public static final Event<LightingStrike> STRUCK_BY_LIGHTING = EventFactory.createArrayBacked(LightingStrike.class, callbacks -> event -> {
		for (LightingStrike callback : callbacks)
			callback.onEntityStruckByLightning(event);
	});

	public static final Event<ProjectileImpact> PROJECTILE_IMPACT = EventFactory.createArrayBacked(ProjectileImpact.class, callbacks -> event -> {
		for (ProjectileImpact callback : callbacks)
			callback.onProjectileImpact(event);
	});

	@FunctionalInterface
	public interface ProjectileImpact {
		void onProjectileImpact(ProjectileImpactEvent event);
	}

	@FunctionalInterface
	public interface EnteringSection {
		void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos);
	}

	@FunctionalInterface
	public interface JoinWorld {
		boolean onJoinWorld(Entity entity, Level world, boolean loadedFromDisk);
	}

	@FunctionalInterface
	public interface Remove {
		void onRemove(Entity entity, RemovalReason reason);
	}

	@FunctionalInterface
	public interface EyeHeight {
		float onEntitySize(Entity entity, float eyeHeight);
	}

	@FunctionalInterface
	public interface LightingStrike {
		void onEntityStruckByLightning(EntityStruckByLightningEvent event);
	}

	@FunctionalInterface
	public interface Teleport {
		void onTeleport(EntityTeleportEvent event);

		class EntityTeleportEvent extends EntityEvents {
			protected double targetX, targetY, targetZ;

			public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
				super(entity);
				this.targetX = targetX;
				this.targetY = targetY;
				this.targetZ = targetZ;
			}

			@Override
			public void sendEvent() {
				EntityEvents.TELEPORT.invoker().onTeleport(this);
			}

			public Entity getEntity() { return entity; }
			public double getTargetX() { return targetX; }
			public void setTargetX(double targetX) { this.targetX = targetX; }
			public double getTargetY() { return targetY; }
			public void setTargetY(double targetY) { this.targetY = targetY; }
			public double getTargetZ() { return targetZ; }
			public void setTargetZ(double targetZ) { this.targetZ = targetZ; }
			public Vec3 getTarget() { return new Vec3(this.targetX, this.targetY, this.targetZ); }
			public double getPrevX() { return getEntity().getX(); }
			public double getPrevY() { return getEntity().getY(); }
			public double getPrevZ() { return getEntity().getZ(); }
			public Vec3 getPrev() { return getEntity().position(); }
		}
	}

	public interface Tracking {
		void onTrackingStart(Entity tracking, ServerPlayer player);
	}

	@FunctionalInterface
	public interface EntitySize {
		void onEntitySizeChange(Size event);
	}

	/**
	 * This event is fired whenever the {@link Pose} changes, and in a few other hardcoded scenarios.<br>
	 * CAREFUL: This is also fired in the Entity constructor. Therefore the entity(subclass) might not be fully initialized. Check Entity#isAddedToWorld() or !Entity#firstUpdate.<br>
	 * If you change the player's size, you probably want to set the eye height accordingly as well<br>
	 **/
	public static class Size extends EntityEvents {
		private final Pose pose;
		private final EntityDimensions oldSize;
		private EntityDimensions newSize;
		private final float oldEyeHeight;
		private float newEyeHeight;

		public Size(Entity entity, Pose pose, EntityDimensions size, float defaultEyeHeight) {
			this(entity, pose, size, size, defaultEyeHeight, defaultEyeHeight);
		}

		public Size(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float oldEyeHeight, float newEyeHeight) {
			super(entity);
			this.pose = pose;
			this.oldSize = oldSize;
			this.newSize = newSize;
			this.oldEyeHeight = oldEyeHeight;
			this.newEyeHeight = newEyeHeight;
		}

		public Pose getPose() {
			return pose;
		}

		public EntityDimensions getOldSize() {
			return oldSize;
		}

		public EntityDimensions getNewSize() {
			return newSize;
		}

		public void setNewSize(EntityDimensions size) {
			setNewSize(size, false);
		}

		/**
		 * Set the new size of the entity. Set updateEyeHeight to true to also update the eye height according to the new size.
		 */
		public void setNewSize(EntityDimensions size, boolean updateEyeHeight) {
			this.newSize = size;
			if (updateEyeHeight) {
				this.newEyeHeight = this.getEntity().getEyeHeight(this.getPose());
			}
		}

		public float getOldEyeHeight() {
			return oldEyeHeight;
		}

		public float getNewEyeHeight() {
			return newEyeHeight;
		}

		public void setNewEyeHeight(float newHeight) {
			this.newEyeHeight = newHeight;
		}

		@Override
		public void sendEvent() {
			SIZE.invoker().onEntitySizeChange(this);
		}
	}
}
