package io.github.fabricators_of_create.porting_lib.entity.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityDataEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMountEvents;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public class EntityMixin implements EntityExtensions {
	// drop capturing

	@Unique
	private List<ItemEntity> capturedDrops = null;

	@WrapWithCondition(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean startCapturingDrops(Level level, Entity entity) {
		if (capturedDrops != null && entity instanceof ItemEntity item) {
			capturedDrops.add(item);
			return false;
		}
		return true;
	}

	@Override
	public void startCapturingDrops() {
		capturedDrops = new ArrayList<>();
	}

	@Override
	public List<ItemEntity> getCapturedDrops() {
		return capturedDrops;
	}

	@Override
	public List<ItemEntity> finishCapturingDrops() {
		List<ItemEntity> captured = capturedDrops;
		capturedDrops = null;
		return captured;
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

	// mount events

	@Shadow
	@Nullable
	private Entity vehicle;

	@Inject(
			method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"
			),
			cancellable = true
	)
	public void onStartRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityMountEvents.MOUNT.invoker().canStartRiding(entity, (Entity) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
	public void onStopRiding(CallbackInfo ci) {
		if (EntityMountEvents.DISMOUNT.invoker().onStopRiding(vehicle, (Entity) (Object) this))
			ci.cancel();
	}
}
