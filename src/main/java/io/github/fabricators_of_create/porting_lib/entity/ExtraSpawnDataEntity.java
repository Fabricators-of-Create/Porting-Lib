package io.github.fabricators_of_create.porting_lib.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface ExtraSpawnDataEntity {
	void readSpawnData(FriendlyByteBuf buf);

	void writeSpawnData(FriendlyByteBuf buf);
}
