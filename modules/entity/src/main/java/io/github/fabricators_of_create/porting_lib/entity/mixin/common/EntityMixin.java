package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.Collection;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.entity.EntityDataKeys;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityInvulnerabilityCheckEvent;

import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.injects.EntityInjection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

@SuppressWarnings("DiscouragedShift") // Why the fuck was this added
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityInjection {
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void entitySizeConstructEvent(EntityType<?> entityType, Level level, CallbackInfo ci) {
		EntityEvents.Size sizeEvent = EntityHooks.getEntitySizeForge((Entity) (Object) this, Pose.STANDING, this.dimensions, this.eyeHeight);
		this.dimensions = sizeEvent.getNewSize();
		this.eyeHeight = sizeEvent.getNewEyeHeight();
		new EntityEvents.EntityConstructing((Entity) (Object) this).sendEvent();
	}

	@WrapOperation(method = "refreshDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
	private EntityDimensions entitySizeEvent(Entity instance, Pose pose, Operation<EntityDimensions> original, @Local(index = 1) EntityDimensions old) {
		EntityDimensions newD = original.call(instance, pose);
		EntityEvents.Size sizeEvent = EntityHooks.getEntitySizeForge(instance, pose, old, newD, newD.eyeHeight());
		return sizeEvent.getNewSize();
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
			method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;canRide(Lnet/minecraft/world/entity/Entity;)Z",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void startRiding(Entity entity, boolean force, boolean sendGameEvent, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityHooks.canMountEntity((Entity) (Object) this, entity, true))
			cir.setReturnValue(false);
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void removeRidingEntity(CallbackInfo ci) {
		if (!EntityHooks.canMountEntity((Entity) (Object) this, this.vehicle, false))
			ci.cancel();
	}

	// custom data

	@Unique
	private CompoundTag port_lib$persistentData;

	@Inject(
			method = "saveWithoutId",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V",
					shift = At.Shift.BEFORE
			)
	)
	private void saveCustomData(ValueOutput output, CallbackInfo ci) {
		output.storeNullable(EntityDataKeys.EXTRA_DATA_KEY, CompoundTag.CODEC, port_lib$persistentData);
	}

	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V",
					shift = At.Shift.BEFORE
			)
	)
	private void loadCustomData(ValueInput input, CallbackInfo ci) {
		input.read(EntityDataKeys.EXTRA_DATA_KEY, CompoundTag.CODEC).ifPresent(neoData -> this.port_lib$persistentData = neoData);
	}

	@Override
	public CompoundTag getPortLibPersistentData() {
		if (port_lib$persistentData == null)
			port_lib$persistentData = new CompoundTag();
		return port_lib$persistentData;
	}

	@WrapOperation(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private void preEntityTick(Entity instance, Operation<Void> original) {
		if (!EntityHooks.fireEntityTickPre(instance).isCanceled()) {
			original.call(instance);
			EntityHooks.fireEntityTickPost(instance);
		}
	}

	// damage events
	@ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
	private boolean checkEntityInvulnerableEvent(boolean original, @Local(argsOnly = true) DamageSource damageSource) {
		EntityInvulnerabilityCheckEvent event = new EntityInvulnerabilityCheckEvent((Entity) (Object) this, damageSource, original);
		event.sendEvent();

		return event.isInvulnerable();
	}
}
