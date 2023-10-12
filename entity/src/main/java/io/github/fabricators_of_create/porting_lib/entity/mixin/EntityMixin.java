package io.github.fabricators_of_create.porting_lib.entity.mixin;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents.EntitySizeEvent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityDataEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMountEvents;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtensions {

	// size event

	@Shadow
	private float eyeHeight;
	@Shadow
	private EntityDimensions dimensions;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void fireSizeEventOnConstructor(EntityType<?> variant, Level world, CallbackInfo ci) {
		EntitySizeEvent event = new EntitySizeEvent((Entity) (Object) this, Pose.STANDING, eyeHeight, dimensions);
		EntityEvents.SIZE.invoker().modifySize(event);
		this.eyeHeight = event.eyeHeight;
		this.dimensions = event.dimensions;
	}

	@ModifyExpressionValue(
			method = "refreshDimensions",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;getEyeHeight(Lnet/minecraft/world/entity/Pose;Lnet/minecraft/world/entity/EntityDimensions;)F"
			)
	)
	private float fireSizeEventOnRefresh(float newEyeHeight,
										@Local(ordinal = 0) EntityDimensions oldDimensions,
										@Local(ordinal = 0) Pose pose,
										@Local(ordinal = 1) EntityDimensions newDimensions) {
		EntitySizeEvent event = new EntitySizeEvent((Entity) (Object) this, pose, eyeHeight, newEyeHeight, oldDimensions, newDimensions);
		EntityEvents.SIZE.invoker().modifySize(event);
		this.dimensions = event.dimensions;
		return event.eyeHeight;
	}


	// custom spawn packets

	@ModifyReturnValue(method = "getAddEntityPacket", at = @At("RETURN"))
	private Packet<ClientGamePacketListener> useExtendedSpawnPacket(Packet<ClientGamePacketListener> base) {
		if (!(this instanceof ExtraSpawnDataEntity extra))
			return base;
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(getId());
		extra.writeSpawnData(buf);
		Packet<ClientGamePacketListener> extraPacket = ServerPlayNetworking.createS2CPacket(ExtraSpawnDataEntity.EXTRA_DATA_PACKET, buf);
		return new ClientboundBundlePacket(List.of(base, extraPacket));
	}

	// drop capturing

	@Unique
	private List<ItemEntity> capturedDrops = null;
	@Unique
	private int capturedDropsCount = 0;

	@WrapWithCondition(
			method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	public boolean captureDroppedItem(Level level, Entity entity) {
		if (capturedDrops != null && entity instanceof ItemEntity item) {
			capturedDrops.add(item);
			return false;
		}
		return true;
	}

	@Override
	public void startCapturingDrops() {
		if (capturedDrops == null && capturedDropsCount != 0) throw new RuntimeException("capturedDropsCount Error at start");
		if (capturedDrops == null) capturedDrops = new ArrayList<>();
		capturedDropsCount++;
	}

	@Override
	public List<ItemEntity> getCapturedDrops() {
		return capturedDrops;
	}

	@Override
	public List<ItemEntity> finishCapturingDrops() {
		if (capturedDrops == null) capturedDrops = new ArrayList<>();
		List<ItemEntity> captured = capturedDrops;
		if (capturedDrops = null || capturedDropsCount <= 0) throw new RuntimeException("capturedDropsCount Error at finish");
		if (capturedDropsCount > 0) capturedDropsCount--;
		if (capturedDropsCount == 0) capturedDrops = new ArrayList<>();
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

	@Shadow
	public abstract int getId();

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
		if (!EntityMountEvents.DISMOUNT.invoker().onStopRiding(vehicle, (Entity) (Object) this))
			ci.cancel();
	}
}
