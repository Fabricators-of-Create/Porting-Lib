package io.github.fabricators_of_create.porting_lib.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * An interface for Entities that need extra information to be communicated
 * between the server and client when they are spawned.
 */
public interface IEntityWithComplexSpawn {
	/**
	 * Called by the server when constructing the spawn packet.
	 * Data should be added to the provided stream.
	 *
	 * @param buffer The packet data stream
	 */
	void writeSpawnData(RegistryFriendlyByteBuf buffer);

	/**
	 * Called by the client when it receives a Entity spawn packet.
	 * Data should be read out of the stream in the same way as it was written.
	 *
	 * @param additionalData The packet data stream
	 */
	void readSpawnData(RegistryFriendlyByteBuf additionalData);
}
