package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;

public interface CustomDataPacketHandlingBlockEntity {
	/**
	 * Called when you receive a {@link ClientboundBlockEntityDataPacket} packet for the location this
	 * BlockEntity is currently in. On the client, the Connection will always
	 * be the remote server. On the server, it will be whomever is responsible for
	 * sending the packet.
	 *
	 * @param connection The Connection the packet originated from
	 * @param input The input received from {@link ClientboundBlockEntityDataPacket}
	 */
	default void onDataPacket(Connection connection, ValueInput input) {
		((BlockEntity) this).loadWithComponents(input);
	}
}
