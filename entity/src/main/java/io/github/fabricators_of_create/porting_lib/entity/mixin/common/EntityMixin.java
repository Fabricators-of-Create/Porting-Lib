package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityDataEvents;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.ITeleporter;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMountEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.MinecartEvents;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions {
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void port_lib$entityInit(EntityType<?> entityType, Level world, CallbackInfo ci) {
		eyeHeight = EntityEvents.EYE_HEIGHT.invoker().onEntitySize((Entity) (Object) this, eyeHeight);
	}

	// CAPTURE DROPS

	@WrapWithCondition(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean port_lib$captureDrops(Level level, Entity entity) {
		if (port_lib$captureDrops != null && entity instanceof ItemEntity item) {
			port_lib$captureDrops.add(item);
			return false;
		}
		return true;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops() {
		return port_lib$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = port_lib$captureDrops;
		port_lib$captureDrops = value;
		return ret;
	}

	@Shadow
	@Nullable
	private Entity vehicle;

	@Shadow
	private float eyeHeight;

	@Shadow
	private Level level;

	@Shadow
	public abstract void unRide();

	@Shadow
	protected abstract void removeAfterChangingDimensions();

	@Shadow
	public abstract boolean isRemoved();

	@Shadow
	@Nullable
	protected abstract PortalInfo findDimensionEntryPoint(ServerLevel destination);

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	private float yRot;

	@Inject(
			method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityMountEvents.MOUNT.invoker().canStartRiding(entity, (Entity) (Object) this)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void port_lib$removeRidingEntity(CallbackInfo ci) {
		if (!EntityMountEvents.DISMOUNT.invoker().onStopRiding(this.vehicle, (Entity) (Object) this)) {
			ci.cancel();
		}
	}

	@Inject(method = "remove", at = @At("TAIL"))
	public void port_lib$onEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
		EntityEvents.ON_REMOVE.invoker().onRemove((Entity) (Object) this, reason);
		if ((Object) this instanceof AbstractMinecart cart) {
			MinecartEvents.REMOVE.invoker().minecartRemove(cart, level);
		}
	}

	@Unique
	@Override
	public Entity changeDimension(ServerLevel p_20118_, ITeleporter teleporter) {
		if (this.level instanceof ServerLevel && !this.isRemoved()) {
			this.level.getProfiler().push("changeDimension");
			this.unRide();
			this.level.getProfiler().push("reposition");
			PortalInfo portalinfo = teleporter.getPortalInfo((Entity) (Object) this, p_20118_, this::findDimensionEntryPoint);
			if (portalinfo == null) {
				return null;
			} else {
				Entity transportedEntity = teleporter.placeEntity((Entity) (Object) this, (ServerLevel) this.level, p_20118_, this.yRot, spawnPortal -> { //Forge: Start vanilla logic
					this.level.getProfiler().popPush("reloading");
					Entity entity = this.getType().create(p_20118_);
					if (entity != null) {
						entity.restoreFrom((Entity) (Object) this);
						entity.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.yRot, entity.getXRot());
						entity.setDeltaMovement(portalinfo.speed);
						p_20118_.addDuringTeleport(entity);
						if (spawnPortal && p_20118_.dimension() == Level.END) {
							ServerLevel.makeObsidianPlatform(p_20118_);
						}
					}
					return entity;
				}); //Forge: End vanilla logic

				this.removeAfterChangingDimensions();
				this.level.getProfiler().pop();
				((ServerLevel)this.level).resetEmptyTime();
				p_20118_.resetEmptyTime();
				this.level.getProfiler().pop();
				return transportedEntity;
			}
		} else {
			return null;
		}
	}

	// custom data

	@Unique
	private CompoundTag customData;

	@Inject(
			method = "saveWithoutId",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
			)
	)
	private void saveCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (customData != null && !customData.isEmpty()) {
			tag.put("ForgeData", customData);
		}
	}

	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
			)
	)
	private void loadCustomData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains("ForgeData")) {
			customData = tag.getCompound("ForgeData");
		}
	}

	@Override
	public CompoundTag getCustomData() {
		if (customData == null)
			customData = new CompoundTag();
		return customData;
	}

	// data events

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	public void afterSave(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
		EntityDataEvents.SAVE.invoker().onSave((Entity) (Object) this, nbt);
	}

	@Inject(method = "load", at = @At("RETURN"))
	public void afterLoad(CompoundTag nbt, CallbackInfo ci) {
		EntityDataEvents.LOAD.invoker().onLoad((Entity) (Object) this, nbt);
	}
}
