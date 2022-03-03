package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.EntityEyeHeightCallback;
import io.github.fabricators_of_create.porting_lib.event.EntityReadExtraDataCallback;
import io.github.fabricators_of_create.porting_lib.event.MinecartEvents;
import io.github.fabricators_of_create.porting_lib.event.StartRidingCallback;
import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import io.github.fabricators_of_create.porting_lib.util.EntityHelper;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, NBTSerializable, RegistryNameProvider {
	@Shadow
	public Level level;
	@Shadow
	private float eyeHeight;
	@Unique
	private CompoundTag port_lib$extraCustomData;
	@Unique
	private Collection<ItemEntity> port_lib$captureDrops = null;
	@Unique
	private ResourceLocation port_lib$registryName = null;

	@Shadow
	protected abstract void readAdditionalSaveData(CompoundTag compoundTag);

	@Shadow
	public abstract EntityType<?> getType();

	@Inject(at = @At("TAIL"), method = "<init>")
	public void port_lib$entityInit(EntityType<?> entityType, Level world, CallbackInfo ci) {
		int newEyeHeight = EntityEyeHeightCallback.EVENT.invoker().onEntitySize((Entity) (Object) this);
		if (newEyeHeight != -1)
			eyeHeight = newEyeHeight;
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
	public Collection<ItemEntity> port_lib$captureDrops() {
		return port_lib$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> port_lib$captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = port_lib$captureDrops;
		port_lib$captureDrops = value;
		return ret;
	}

	// EXTRA CUSTOM DATA

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void port_lib$beforeWriteCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (port_lib$extraCustomData != null && !port_lib$extraCustomData.isEmpty()) {
			PortingLib.LOGGER.debug("writing custom data to entity [{}]", this);
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
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void port_lib$spawnSprintParticle(CallbackInfo ci, int i, int j, int k, BlockPos blockPos) {
		BlockState state = level.getBlockState(blockPos);
		if (state.getBlock() instanceof CustomRunningEffectsBlock custom) {
			if (custom.addRunningEffects(state, level, blockPos, (Entity) (Object) this)) ci.cancel();
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
	public void port_lib$startRiding(Entity mounted, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (StartRidingCallback.EVENT.invoker().onStartRiding(mounted, (Entity) (Object) this) == InteractionResult.FAIL) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "setRemoved", at = @At("RETURN"))
	private void port_lib$setRemoved(RemovalReason reason, CallbackInfo ci) {
		if ((Object) this instanceof AbstractMinecart cart) {
			MinecartEvents.REMOVE.invoker().minecartRemove(cart, level);
		}
	}

	@Unique
	@Override
	public CompoundTag port_lib$getExtraCustomData() {
		if (port_lib$extraCustomData == null) {
			port_lib$extraCustomData = new CompoundTag();
		}
		return port_lib$extraCustomData;
	}

	@Unique
	@Override
	public CompoundTag port_lib$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		String id = EntityHelper.getEntityString(MixinHelper.cast(this));

		if (id != null) {
			nbt.putString("id", id);
		}

		return nbt;
	}

	@Unique
	@Override
	public void port_lib$deserializeNBT(CompoundTag nbt) {
		readAdditionalSaveData(nbt);
	}

	@Override
	public ResourceLocation getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.ENTITY_TYPE.getKey(getType());
		}
		return port_lib$registryName;
	}
}
