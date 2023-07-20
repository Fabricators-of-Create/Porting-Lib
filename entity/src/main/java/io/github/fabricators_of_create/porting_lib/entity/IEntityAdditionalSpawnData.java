package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * An interface to implement onto Entities to have custom data synced on spawn.
 * To use, implement the required methods, and have your getAddEntityPacket return createExtraDataSpawnPacket(this).
 */
public interface IEntityAdditionalSpawnData {
	ResourceLocation EXTRA_DATA_PACKET = PortingLib.id("extra_entity_spawn_data");

	/**
	 * Called by the client when it receives a Entity spawn packet.
	 * Data should be read out of the stream in the same way as it was written.
	 *
	 * @param buf The packet data stream
	 */
	void readSpawnData(FriendlyByteBuf buf);

	/**
	 * Called by the server when constructing the spawn packet.
	 * Data should be added to the provided stream.
	 *
	 * @param buf The packet data stream
	 */
	void writeSpawnData(FriendlyByteBuf buf);
}
