package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ChunkTrackingCallback;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
	@Shadow
	@Final
	private ServerLevel level;

	@Inject(method = "updateChunkTracking", at = @At(value = "JUMP", opcode = Opcodes.IFEQ))
	public void port_lib$startTrackingChunk(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load, CallbackInfo ci) {
		if (wasLoaded != load) {
			if (load)
				ChunkTrackingCallback.WATCH.invoker().onChunkWatch(player, chunkPos, this.level);
			else
				ChunkTrackingCallback.UNWATCH.invoker().onChunkUnwatch(player, chunkPos, this.level);
		}
	}
}
