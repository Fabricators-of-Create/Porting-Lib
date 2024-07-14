package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface CustomDataPacketHandlingBlockEntity {
	/**
	 * Called when you receive a {@link ClientboundBlockEntityDataPacket} packet for the location this
	 * BlockEntity is currently in. On the client, the Connection will always
	 * be the remote server. On the server, it will be whomever is responsible for
	 * sending the packet.
	 *
	 * @param net The Connection the packet originated from
	 * @param pkt The data packet
	 */
	default void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		CompoundTag compoundtag = pkt.getTag();
		if (!compoundtag.isEmpty()) {
			((BlockEntity) this).loadWithComponents(compoundtag, lookupProvider);
		}
	}
}
