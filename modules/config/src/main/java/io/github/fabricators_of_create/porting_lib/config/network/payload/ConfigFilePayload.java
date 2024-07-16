package io.github.fabricators_of_create.porting_lib.config.network.payload;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * A payload that contains a config file.
 * <p>
 * This is used to send config files to the client.
 * </p>
 *
 * @param fileName The name of the config file.
 * @param contents The contents of the config file.
 */
@ApiStatus.Internal
public record ConfigFilePayload(String fileName, byte[] contents) implements CustomPacketPayload {
	public static final Type<ConfigFilePayload> TYPE = new Type<>(PortingLib.id("config_file"));
	public static final StreamCodec<FriendlyByteBuf, ConfigFilePayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			ConfigFilePayload::fileName,
			ByteBufCodecs.BYTE_ARRAY,
			ConfigFilePayload::contents,
			ConfigFilePayload::new);

	@Override
	public Type<ConfigFilePayload> type() {
		return TYPE;
	}
}
