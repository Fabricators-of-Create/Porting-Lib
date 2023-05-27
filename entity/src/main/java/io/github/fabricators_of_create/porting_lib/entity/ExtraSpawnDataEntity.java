package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * An interface to implement onto Entities to have custom data synced on spawn.
 * To use, implement the required methods, and have your getAddEntityPacket return createExtraDataSpawnPacket(this).
 */
public interface ExtraSpawnDataEntity {
	ResourceLocation EXTRA_DATA_PACKET = PortingLib.id("extra_entity_spawn_data");

	void readSpawnData(FriendlyByteBuf buf);

	void writeSpawnData(FriendlyByteBuf buf);
}
