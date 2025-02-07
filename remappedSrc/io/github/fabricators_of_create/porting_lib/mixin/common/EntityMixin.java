package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.EntityReadExtraDataCallback;
import io.github.fabricators_of_create.porting_lib.event.common.MinecartEvents;
import io.github.fabricators_of_create.porting_lib.event.common.MountEntityCallback;
import io.github.fabricators_of_create.porting_lib.extensions.BlockParticleOptionExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ITeleporter;
import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import io.github.fabricators_of_create.porting_lib.util.EntityHelper;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Collection;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, INBTSerializable<NbtCompound>, RegistryNameProvider {
	@Shadow
	public World level;
	@Shadow
	private float eyeHeight;
	@Unique
	private NbtCompound port_lib$extraCustomData;
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;
	@Unique
	private Identifier port_lib$registryName = null;

	@Shadow
	protected abstract void readAdditionalSaveData(NbtCompound compoundTag);

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	@Nullable
	private Entity vehicle;

	@Shadow
	protected abstract void removeAfterChangingDimensions();

	@Shadow
	@Nullable
	protected abstract TeleportTarget findDimensionEntryPoint(ServerWorld destination);

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
	public abstract NbtCompound saveWithoutId(NbtCompound compoundTag);

	@Shadow
	public abstract void load(NbtCompound compoundTag);

	@Shadow
	public float maxUpStep;

	@Shadow
	public abstract Vec3d position();

	@Inject(at = @At("TAIL"), method = "<init>")
	public void port_lib$entityInit(EntityType<?> entityType, World world, CallbackInfo ci) {
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
	public void port_lib$beforeWriteCustomData(NbtCompound tag, CallbackInfoReturnable<NbtCompound> cir) {
		if (port_lib$extraCustomData != null && !port_lib$extraCustomData.isEmpty()) {
			tag.put(EntityHelper.EXTRA_DATA_KEY, port_lib$extraCustomData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeReadCustomData(NbtCompound tag, CallbackInfo ci) {
		if (tag.contains(EntityHelper.EXTRA_DATA_KEY)) {
			port_lib$extraCustomData = tag.getCompound(EntityHelper.EXTRA_DATA_KEY);
		} else if (tag.contains("create_ExtraEntityData")) { // legacy check, should be removed on release
			port_lib$extraCustomData = tag.getCompound("create_ExtraEntityData");
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
		if (MountEntityCallback.EVENT.invoker().onStartRiding(entity, (Entity) (Object) this, true) == ActionResult.FAIL) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void port_lib$removeRidingEntity(CallbackInfo ci) {
		if (MountEntityCallback.EVENT.invoker().onStartRiding(this.vehicle, (Entity) (Object) this, false) == ActionResult.FAIL) {
			ci.cancel();
		}
	}

	@Inject(method = "remove", at = @At("TAIL"))
	public void port_lib$onEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
		EntityEvents.ON_REMOVE.invoker().onRemove((Entity) (Object) this, reason);
		if ((Object) this instanceof AbstractMinecartEntity cart) {
			MinecartEvents.REMOVE.invoker().minecartRemove(cart, level);
		}
	}

	@Unique
	@Override
	public NbtCompound getExtraCustomData() {
		if (port_lib$extraCustomData == null) {
			port_lib$extraCustomData = new NbtCompound();
		}
		return port_lib$extraCustomData;
	}

	@Unique
	@Override
	public NbtCompound serializeNBT() {
		NbtCompound ret = new NbtCompound();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", id);
		}
		return saveWithoutId(ret);
	}

	@Unique
	@Override
	public void deserializeNBT(NbtCompound nbt) {
		load(nbt);
	}

	@Unique
	@Override
	public Identifier getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.ENTITY_TYPE.getId(getType());
		}
		return port_lib$registryName;
	}

	@Unique
	@Override
	public Entity changeDimension(ServerWorld p_20118_, ITeleporter teleporter) {
		if (this.level instanceof ServerWorld && !this.isRemoved()) {
			this.level.getProfiler().push("changeDimension");
			this.unRide();
			this.level.getProfiler().push("reposition");
			TeleportTarget portalinfo = teleporter.getPortalInfo((Entity) (Object) this, p_20118_, this::findDimensionEntryPoint);
			if (portalinfo == null) {
				return null;
			} else {
				Entity transportedEntity = teleporter.placeEntity((Entity) (Object) this, (ServerWorld) this.level, p_20118_, this.yRot, spawnPortal -> { //Forge: Start vanilla logic
					this.level.getProfiler().swap("reloading");
					Entity entity = this.getType().create(p_20118_);
					if (entity != null) {
						entity.copyFrom((Entity) (Object) this);
						entity.refreshPositionAndAngles(portalinfo.position.x, portalinfo.position.y, portalinfo.position.z, portalinfo.yaw, entity.getPitch());
						entity.setVelocity(portalinfo.velocity);
						p_20118_.onDimensionChanged(entity);
						if (spawnPortal && p_20118_.getRegistryKey() == World.END) {
							ServerWorld.createEndSpawnPlatform(p_20118_);
						}
					}
					return entity;
				}); //Forge: End vanilla logic

				this.removeAfterChangingDimensions();
				this.level.getProfiler().pop();
				((ServerWorld)this.level).resetIdleTimeout();
				p_20118_.resetIdleTimeout();
				this.level.getProfiler().pop();
				return transportedEntity;
			}
		} else {
			return null;
		}
	}

	@Inject(method = "collide", at = @At(value = "JUMP", opcode = Opcodes.IFGE))
	public void port_lib$modifyStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
		this.maxUpStep = this.getStepHeight();
	}

	@ModifyArg(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
			)
	)
	private ParticleEffect addSourcePos(ParticleEffect options) {
		return BlockParticleOptionExtensions.setSourceFromEntity(options, (Entity) (Object) this);
	}
}
