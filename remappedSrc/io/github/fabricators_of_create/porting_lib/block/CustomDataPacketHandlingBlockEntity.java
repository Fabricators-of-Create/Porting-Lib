package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public interface CustomDataPacketHandlingBlockEntity {
	void onDataPacket(ClientConnection connection, BlockEntityUpdateS2CPacket packet);
}
