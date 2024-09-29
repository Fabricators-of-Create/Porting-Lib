package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.Collection;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityDataEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.MinecartEvents;
import io.github.fabricators_of_create.porting_lib.entity.ext.EntityExt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExt {
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void entitySizeConstructEvent(EntityType<?> entityType, Level level, CallbackInfo ci) {
		EntityEvent.Size sizeEvent = EntityHooks.getEntitySizeForge((Entity) (Object) this, Pose.STANDING, this.dimensions, this.eyeHeight);
		this.dimensions = sizeEvent.getNewSize();
		this.eyeHeight = sizeEvent.getNewEyeHeight();
		new EntityEvent.EntityConstructing((Entity) (Object) this).sendEvent();
	}

	@WrapOperation(method = "refreshDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
	private EntityDimensions entitySizeEvent(Entity instance, Pose pose, Operation<EntityDimensions> original, @Local(index = 1) EntityDimensions old) {
		EntityDimensions newD = original.call(instance, pose);
		EntityEvent.Size sizeEvent = EntityHooks.getEntitySizeForge(instance, pose, old, newD, newD.eyeHeight());
		return sizeEvent.getNewSize();
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
	private Level level;

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	public abstract int getId();

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	public abstract float getEyeHeight();

	@Shadow
	private float eyeHeight;

	@Inject(
			method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;canRide(Lnet/minecraft/world/entity/Entity;)Z",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityHooks.canMountEntity((Entity) (Object) this, entity, true))
			cir.setReturnValue(false);
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void port_lib$removeRidingEntity(CallbackInfo ci) {
		if (!EntityHooks.canMountEntity((Entity) (Object) this, this.vehicle, false))
			ci.cancel();
	}

	@Inject(method = "remove", at = @At("TAIL"))
	public void port_lib$onEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
//		EntityEvent.ON_REMOVE.invoker().onRemove((Entity) (Object) this, reason); TODO: PORT
		if ((Object) this instanceof AbstractMinecart cart) {
			MinecartEvents.REMOVE.invoker().minecartRemove(cart, level);
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

	@WrapOperation(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private void preEntityTick(Entity instance, Operation<Void> original) {
		if (!EntityHooks.fireEntityTickPre(instance).isCanceled()) {
			original.call(instance);
			EntityHooks.fireEntityTickPost(instance);
		}
	}
}
