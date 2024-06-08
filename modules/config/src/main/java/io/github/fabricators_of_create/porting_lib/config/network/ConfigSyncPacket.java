package io.github.fabricators_of_create.porting_lib.config.network;

import io.github.fabricators_of_create.porting_lib.config.PortingLibConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConfigSyncPacket(String name, byte[] data) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, ConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ConfigSyncPacket::name, ByteBufCodecs.BYTE_ARRAY, ConfigSyncPacket::data, ConfigSyncPacket::new
	);
	public static final Type<ConfigSyncPacket> TYPE = new Type<>(PortingLibConfig.CONFIG_SYNC);

	private ConfigSyncPacket(FriendlyByteBuf friendlyByteBuf) {
		this(friendlyByteBuf.readUtf(), friendlyByteBuf.readByteArray());
	}

	private void write(FriendlyByteBuf friendlyByteBuf) {
		friendlyByteBuf.writeUtf(this.name);
		friendlyByteBuf.writeByteArray(this.data);
	}

	@Override
	public Type<ConfigSyncPacket> type() {
		return TYPE;
	}
}
