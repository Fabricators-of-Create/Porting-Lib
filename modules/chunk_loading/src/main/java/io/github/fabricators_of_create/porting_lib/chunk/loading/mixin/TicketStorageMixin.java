package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkLoading;
import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.TicketStorageExtension;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Predicate;

@Mixin(TicketStorage.class)
public abstract class TicketStorageMixin implements TicketStorageExtension {
	@Shadow
	protected abstract LongSet getAllChunksWithTicketThat(Predicate<Ticket> predicate);

	// Neo: Keep track of forced loaded chunks caused by entities or blocks.
	private final ForcedChunkManager.TicketTracker<BlockPos> port_lib$blockForcedChunks = new ForcedChunkManager.TicketTracker<>((TicketStorage) (Object) this, PortingLibChunkLoading.BLOCK_TICKET, PortingLibChunkLoading.BLOCK_WITH_NATURAL_SPAWNING_TICKET);
	private final ForcedChunkManager.TicketTracker<UUID> port_lib$entityForcedChunks = new ForcedChunkManager.TicketTracker<>((TicketStorage) (Object) this, PortingLibChunkLoading.ENTITY_TICKET, PortingLibChunkLoading.ENTITY_WITH_NATURAL_SPAWNING_TICKET);
	private LongSet port_lib$chunksWithForceNaturalSpawning = new LongOpenHashSet();

	public ForcedChunkManager.TicketTracker<BlockPos> port_lib$getBlockForcedChunks() {
		return this.port_lib$blockForcedChunks;
	}

	public ForcedChunkManager.TicketTracker<UUID> port_lib$getEntityForcedChunks() {
		return this.port_lib$entityForcedChunks;
	}

	private void port_lib$updateForcedNaturalSpawning() {
		this.port_lib$chunksWithForceNaturalSpawning = this.getAllChunksWithTicketThat(ticket -> ticket.getType().port_lib$forceNaturalSpawning());
	}

	public boolean port_lib$shouldForceNaturalSpawning(ChunkPos chunkPos) {
		return port_lib$chunksWithForceNaturalSpawning.contains(chunkPos.toLong());
	}

	@Inject(method = "deactivateTicketsOnClosing", at = @At("TAIL"))
	private void deactivatePortingLibTicketsOnClosing(CallbackInfo ci) {
		this.port_lib$blockForcedChunks.deactivateTicketsOnClosing();
		this.port_lib$entityForcedChunks.deactivateTicketsOnClosing();
	}
}
