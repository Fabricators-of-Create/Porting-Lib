package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface CustomDataPacketHandlingBlockEntity {
	void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet);
}
