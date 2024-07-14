package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * EntityTeleportEvent is fired when an event involving any teleportation of an Entity occurs.<br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #getTarget()} contains the target destination.<br>
 * {@link #getPrev()} contains the entity's current position.<br>
 * <br>
 **/
public abstract class EntityTeleportEvent extends EntityEvent implements CancellableEvent {
	protected double targetX;
	protected double targetY;
	protected double targetZ;

	public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
		super(entity);
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
	}

	public double getTargetX() {
		return targetX;
	}

	public void setTargetX(double targetX) {
		this.targetX = targetX;
	}

	public double getTargetY() {
		return targetY;
	}

	public void setTargetY(double targetY) {
		this.targetY = targetY;
	}

	public double getTargetZ() {
		return targetZ;
	}

	public void setTargetZ(double targetZ) {
		this.targetZ = targetZ;
	}

	public Vec3 getTarget() {
		return new Vec3(this.targetX, this.targetY, this.targetZ);
	}

	public double getPrevX() {
		return getEntity().getX();
	}

	public double getPrevY() {
		return getEntity().getY();
	}

	public double getPrevZ() {
		return getEntity().getZ();
	}

	public Vec3 getPrev() {
		return getEntity().position();
	}

	/**
	 * EntityTeleportEvent.TeleportCommand is fired before a living entity is teleported
	 * from use of {@link net.minecraft.server.commands.TeleportCommand}.
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If the event is not canceled, the entity will be teleported.
	 * <br>
	 * This event is only fired on the {@link EnvType#SERVER} side.<br>
	 * <br>
	 * If this event is canceled, the entity will not be teleported.
	 */
	public static class TeleportCommand extends EntityTeleportEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onTeleportCommand(event);
		});

		public TeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onTeleportCommand(this);
		}

		public interface Callback {
			void onTeleportCommand(TeleportCommand event);
		}
	}

	/**
	 * EntityTeleportEvent.SpreadPlayersCommand is fired before a living entity is teleported
	 * from use of {@link net.minecraft.server.commands.SpreadPlayersCommand}.
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If the event is not canceled, the entity will be teleported.
	 * <br>
	 * This event is only fired on the {@link EnvType#SERVER} side.<br>
	 * <br>
	 * If this event is canceled, the entity will not be teleported.
	 */
	public static class SpreadPlayersCommand extends EntityTeleportEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onSpreadPlayersCommand(event);
		});

		public SpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onSpreadPlayersCommand(this);
		}

		public interface Callback {
			void onSpreadPlayersCommand(SpreadPlayersCommand event);
		}
	}

	/**
	 * EntityTeleportEvent.EnderEntity is fired before an Enderman or Shulker randomly teleports.
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If the event is not canceled, the entity will be teleported.
	 * <br>
	 * This event is only fired on the {@link EnvType#SERVER} side.<br>
	 * <br>
	 * If this event is canceled, the entity will not be teleported.
	 */
	public static class EnderEntity extends EntityTeleportEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onEnderEntityTeleport(event);
		});

		private final LivingEntity entityLiving;

		public EnderEntity(LivingEntity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
			this.entityLiving = entity;
		}

		public LivingEntity getEntityLiving() {
			return entityLiving;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onEnderEntityTeleport(this);
		}

		public interface Callback {
			void onEnderEntityTeleport(EnderEntity event);
		}
	}

	/**
	 * EntityTeleportEvent.EnderPearl is fired before an Entity is teleported from an EnderPearlEntity.
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If the event is not canceled, the entity will be teleported.
	 * <br>
	 * This event is only fired on the {@link EnvType#SERVER} side.<br>
	 * <br>
	 * If this event is canceled, the entity will not be teleported.
	 */
	public static class EnderPearl extends EntityTeleportEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onEnderPearlTeleport(event);
		});

		private final ServerPlayer player;
		private final ThrownEnderpearl pearlEntity;
		private float attackDamage;
		private final HitResult hitResult;

		@ApiStatus.Internal
		public EnderPearl(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage, HitResult hitResult) {
			super(entity, targetX, targetY, targetZ);
			this.pearlEntity = pearlEntity;
			this.player = entity;
			this.attackDamage = attackDamage;
			this.hitResult = hitResult;
		}

		public ThrownEnderpearl getPearlEntity() {
			return pearlEntity;
		}

		public ServerPlayer getPlayer() {
			return player;
		}

		@Nullable
		public HitResult getHitResult() {
			return this.hitResult;
		}

		public float getAttackDamage() {
			return attackDamage;
		}

		public void setAttackDamage(float attackDamage) {
			this.attackDamage = attackDamage;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onEnderPearlTeleport(this);
		}

		public interface Callback {
			void onEnderPearlTeleport(EnderPearl event);
		}
	}

	/**
	 * EntityTeleportEvent.ChorusFruit is fired before a LivingEntity is teleported due to consuming Chorus Fruit.
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If the event is not canceled, the entity will be teleported.
	 * <br>
	 * This event is only fired on the {@link EnvType#SERVER} side.<br>
	 * <br>
	 * If this event is canceled, the entity will not be teleported.
	 */
	public static class ChorusFruit extends EntityTeleportEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onChorusFruitTeleport(event);
		});

		private final LivingEntity entityLiving;

		public ChorusFruit(LivingEntity entity, double targetX, double targetY, double targetZ) {
			super(entity, targetX, targetY, targetZ);
			this.entityLiving = entity;
		}

		public LivingEntity getEntityLiving() {
			return entityLiving;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onChorusFruitTeleport(this);
		}

		public interface Callback {
			void onChorusFruitTeleport(ChorusFruit event);
		}
	}
}
