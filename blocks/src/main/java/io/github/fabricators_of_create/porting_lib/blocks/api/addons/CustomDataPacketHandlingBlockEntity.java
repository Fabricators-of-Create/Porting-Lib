package io.github.fabricators_of_create.porting_lib.blocks.api.addons;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface CustomDataPacketHandlingBlockEntity {
	/**
	 * Called when you receive a {@link ClientboundBlockEntityDataPacket} packet for the location this
	 * BlockEntity is currently in. On the client, the {@link Connection} will always
	 * be the remote server. On the server, it will be whoever is responsible for
	 * sending the packet.
	 *
	 * @param connection The {@link Connection} the packet originated from
	 * @param packet The data packet
	 */
	void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet);
}
