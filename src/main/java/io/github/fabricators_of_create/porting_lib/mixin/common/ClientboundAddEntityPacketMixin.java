package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.extensions.ClientboundAddEntityPacketExtensions;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

// Use random magic value to ensure that custom data is written and read in the correct order if other mixins inject in the same place
@Mixin(value = ClientboundAddEntityPacket.class, priority = 32455)
public abstract class ClientboundAddEntityPacketMixin implements ClientboundAddEntityPacketExtensions {
	@Unique
	private FriendlyByteBuf port_lib$extraDataBuf;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;I)V", at = @At("TAIL"))
	public void port_lib$onEntityInit(Entity entity, int entityData, CallbackInfo ci) {
		port_lib$setExtraData(entity);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/EntityType;ILnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	public void port_lib$onEntityInit(Entity entity, EntityType<?> entityType, int data, BlockPos pos, CallbackInfo ci) {
		port_lib$setExtraData(entity);
	}

	@Unique
	private void port_lib$setExtraData(Entity entity) {
		if (entity instanceof ExtraSpawnDataEntity extra) {
			port_lib$extraDataBuf = PacketByteBufs.create();
			extra.writeSpawnData(port_lib$extraDataBuf);
		}
	}

	@Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	public void port_lib$onTailWrite(FriendlyByteBuf buf, CallbackInfo ci) {
		if (port_lib$extraDataBuf != null) {
			buf.writeBoolean(true);
			buf.writeVarInt(port_lib$extraDataBuf.writerIndex());
			buf.writeBytes(port_lib$extraDataBuf);
		} else {
			buf.writeBoolean(false);
		}
	}

	@Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
	public void port_lib$onTailRead(FriendlyByteBuf buf, CallbackInfo ci) {
		boolean hasExtraData = buf.readBoolean();
		if (hasExtraData) {
			int readable = buf.readVarInt();
			if (readable != 0) {
				port_lib$extraDataBuf = new FriendlyByteBuf(buf.readBytes(readable));
			}
		}
	}

	@Unique
	@Override
	public FriendlyByteBuf getExtraDataBuf() {
		return port_lib$extraDataBuf;
	}
}
