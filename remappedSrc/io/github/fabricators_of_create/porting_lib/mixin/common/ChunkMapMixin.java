package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ChunkTrackingCallback;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ChunkMapMixin {
	@Shadow
	@Final
	ServerWorld level;

	@Inject(method = "updateChunkTracking", at = @At(value = "JUMP", opcode = Opcodes.IFEQ))
	public void port_lib$startTrackingChunk(ServerPlayerEntity player, ChunkPos chunkPos, MutableObject<ChunkDataS2CPacket> packetCache, boolean wasLoaded, boolean load, CallbackInfo ci) {
		if (wasLoaded != load) {
			if (load)
				ChunkTrackingCallback.WATCH.invoker().onChunkWatch(player, chunkPos, this.level);
			else
				ChunkTrackingCallback.UNWATCH.invoker().onChunkUnwatch(player, chunkPos, this.level);
		}
	}
}
