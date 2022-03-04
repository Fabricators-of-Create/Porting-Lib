package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.StartRidingCallback;
import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import io.github.fabricators_of_create.porting_lib.util.EntityHelper;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions, NBTSerializable, RegistryNameProvider {
	@Shadow
	public Level level;
	@Shadow
	private float eyeHeight;
	@Unique
	private CompoundTag extraCustomData;
	@Unique
	private Collection<ItemEntity> captureDrops = null;

	@Shadow
	protected abstract void readAdditionalSaveData(CompoundTag compoundTag);

	@Shadow
	public abstract EntityType<?> getType();

	@Inject(at = @At("TAIL"), method = "<init>")
	public void entityInit(EntityType<?> entityType, Level world, CallbackInfo ci) {
		int newEyeHeight = EntityEvents.EYE_HEIGHT.invoker().onEntitySize((Entity) (Object) this);
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
	public void spawnAtLocation(ItemStack stack, float f, CallbackInfoReturnable<ItemEntity> cir, ItemEntity itemEntity) {
		if (captureDrops != null) captureDrops.add(itemEntity);
		else cir.cancel();
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops() {
		return captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = captureDrops;
		captureDrops = value;
		return ret;
	}

	// EXTRA CUSTOM DATA

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void beforeWriteCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		if (extraCustomData != null && !extraCustomData.isEmpty()) {
			tag.put(EntityHelper.EXTRA_DATA_KEY, extraCustomData);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void beforeReadCustomData(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(EntityHelper.EXTRA_DATA_KEY)) {
			extraCustomData = tag.getCompound(EntityHelper.EXTRA_DATA_KEY);
		}
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
	public void spawnSprintParticle(CallbackInfo ci, int i, int j, int k, BlockPos pos, BlockState state) {
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
	public void startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (StartRidingCallback.EVENT.invoker().onStartRiding(MixinHelper.cast(this), entity) == InteractionResult.FAIL) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "remove", at = @At("TAIL"))
	public void onEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
		EntityEvents.ON_REMOVE.invoker().onRemove((Entity) (Object) this, reason);
	}

	@Unique
	@Override
	public CompoundTag getExtraCustomData() {
		if (extraCustomData == null) {
			extraCustomData = new CompoundTag();
		}
		return extraCustomData;
	}

	@Unique
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		String id = EntityHelper.getEntityString(MixinHelper.cast(this));

		if (id != null) {
			nbt.putString("id", id);
		}

		return nbt;
	}

	@Unique
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		readAdditionalSaveData(nbt);
	}

	@Unique
	@Override
	public ResourceLocation getRegistryName() {
		return Registry.ENTITY_TYPE.getKey(getType());
	}
}
