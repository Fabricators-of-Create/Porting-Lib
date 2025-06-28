package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ChunkWatchEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
public class PlayerChunkSenderMixin {
	@Inject(method = "sendChunk", at = @At("TAIL"))
	private static void sentChunkEvent(ServerGamePacketListenerImpl packetListener, ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
		new ChunkWatchEvent.Sent(packetListener.player, chunk, level).sendEvent();
	}
}
