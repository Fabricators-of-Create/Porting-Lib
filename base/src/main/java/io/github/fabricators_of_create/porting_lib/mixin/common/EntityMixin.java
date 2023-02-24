package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;

import io.github.fabricators_of_create.porting_lib.util.EntityHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.EntityReadExtraDataCallback;
import io.github.fabricators_of_create.porting_lib.event.common.MinecartEvents;
import io.github.fabricators_of_create.porting_lib.event.common.MountEntityCallback;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.ITeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, INBTSerializableCompound {
	@Shadow
	public Level level;
	@Shadow
	private float eyeHeight;
	@Unique
	private CompoundTag port_lib$extraCustomData;
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;

	@Shadow
	protected abstract void readAdditionalSaveData(CompoundTag compoundTag);

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	@Nullable
	private Entity vehicle;

	@Shadow
	protected abstract void removeAfterChangingDimensions();

	@Shadow
	@Nullable
	protected abstract PortalInfo findDimensionEntryPoint(ServerLevel destination);

	@Shadow
	private float yRot;

	@Shadow
	public abstract boolean isRemoved();

	@Shadow
	public abstract void unRide();

	@Shadow
	@Nullable
	protected abstract String getEncodeId();

	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);

	@Shadow
	public abstract void load(CompoundTag compoundTag);

	@Shadow
	public float maxUpStep;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void port_lib$entityInit(EntityType<?> entityType, Level world, CallbackInfo ci) {
		eyeHeight = EntityEvents.EYE_HEIGHT.invoker().onEntitySize((Entity) (Object) this, eyeHeight);
	}

	// CAPTURE DROPS

	@Inject(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V",
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	public void port_lib$spawnAtLocation(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, ItemEntity itemEntity) {
		if (port_lib$captureDrops != null) {
			port_lib$captureDrops.add(itemEntity);
			cir.setReturnValue(itemEntity);
		}
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

	// EXTRA CUSTOM DATA

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeWriteCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (port_lib$extraCustomData != null && !port_lib$extraCustomData.isEmpty()) {
			tag.put(EntityHelper.EXTRA_DATA_KEY, port_lib$extraCustomData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeReadCustomData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(EntityHelper.EXTRA_DATA_KEY)) {
			port_lib$extraCustomData = tag.getCompound(EntityHelper.EXTRA_DATA_KEY);
		}
		EntityReadExtraDataCallback.EVENT.invoker().onLoad((Entity) (Object) this, port_lib$extraCustomData);
	}

	// RUNNING EFFECTS

	@Inject(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.BY,
					by = 2
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void port_lib$spawnSprintParticle(CallbackInfo ci, int i, int j, int k, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CustomRunningEffectsBlock custom &&
				custom.addRunningEffects(state, level, pos, (Entity) (Object) this)) {
			ci.cancel();
		}
	}

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
		if (MountEntityCallback.EVENT.invoker().onStartRiding(entity, (Entity) (Object) this, true) == InteractionResult.FAIL) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void port_lib$removeRidingEntity(CallbackInfo ci) {
		if (MountEntityCallback.EVENT.invoker().onStartRiding(this.vehicle, (Entity) (Object) this, false) == InteractionResult.FAIL) {
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
	public CompoundTag getExtraCustomData() {
		if (port_lib$extraCustomData == null) {
			port_lib$extraCustomData = new CompoundTag();
		}
		return port_lib$extraCustomData;
	}

	@Unique
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", id);
		}
		return saveWithoutId(ret);
	}

	@Unique
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		load(nbt);
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
}
