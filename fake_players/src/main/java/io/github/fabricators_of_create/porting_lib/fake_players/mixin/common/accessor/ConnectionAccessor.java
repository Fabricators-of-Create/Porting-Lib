package io.github.fabricators_of_create.porting_lib.fake_players.mixin.common.accessor;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public interface ConnectionAccessor {
	@Accessor("channel")
	void port_lib$channel(Channel channel);
}
