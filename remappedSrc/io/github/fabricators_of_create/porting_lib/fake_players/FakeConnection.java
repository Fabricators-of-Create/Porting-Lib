package io.github.fabricators_of_create.porting_lib.fake_players;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.ConnectionAccessor;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

public class FakeConnection extends ClientConnection {
	public FakeConnection(NetworkSide packetFlow) {
		super(packetFlow);
		// this fixes a crash with adventure-platform-fabric
		// yoinked from Carpet: https://github.com/gnembon/fabric-carpet/pull/1235/files#diff-47be3b4fb4b7936e3af3566dd91bfedaecffc990576bbdce097b8a31f164eb47
		((ConnectionAccessor) this).port_lib$channel(new EmbeddedChannel());
	}
}
