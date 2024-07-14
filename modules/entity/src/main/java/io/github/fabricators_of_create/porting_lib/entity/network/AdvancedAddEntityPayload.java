package io.github.fabricators_of_create.porting_lib.entity.network;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.IEntityWithComplexSpawn;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Payload that can be sent from the server to the client to add an entity to the world, with custom data.
 *
 * @param entityId      The id of the entity to add.
 * @param customPayload The custom data of the entity to add.
 */
@ApiStatus.Internal
public record AdvancedAddEntityPayload(int entityId, byte[] customPayload) implements CustomPacketPayload {

	public static final Type<AdvancedAddEntityPayload> TYPE = new Type<>(PortingLib.id("advanced_add_entity"));
	public static final StreamCodec<FriendlyByteBuf, AdvancedAddEntityPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			AdvancedAddEntityPayload::entityId,
			ByteBufCodecs.BYTE_ARRAY,
			AdvancedAddEntityPayload::customPayload,
			AdvancedAddEntityPayload::new);
	public AdvancedAddEntityPayload(Entity e) {
		this(e.getId(), writeCustomData(e));
	}

	private static byte[] writeCustomData(final Entity entity) {
		if (!(entity instanceof IEntityWithComplexSpawn additionalSpawnData)) {
			return new byte[0];
		}

		final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), entity.registryAccess());
		try {
			additionalSpawnData.writeSpawnData(buf);
			return buf.array();
		} finally {
			buf.release();
		}
	}

	@Override
	public Type<AdvancedAddEntityPayload> type() {
		return TYPE;
	}
}
