package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityEvents {
	public static final Event<EyeHeight> EYE_HEIGHT = EventFactory.createArrayBacked(EyeHeight.class, callbacks -> (entity, height) -> {
		for (EyeHeight callback : callbacks) {
			float newHeight = callback.onEntitySize(entity, height);
			if (newHeight != height)
				return newHeight;
		}

		return height;
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
	public interface Teleport {
		void onTeleport(EntityTeleportEvent event);

		class EntityTeleportEvent extends EntityEvent {
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
}
