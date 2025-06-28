package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

import net.minecraft.world.level.TicketStorage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
	@Shadow
	@Final
	private TicketStorage ticketStorage;

	@ModifyExpressionValue(method = "collectSpawningChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;anyPlayerCloseEnoughForSpawningInternal(Lnet/minecraft/world/level/ChunkPos;)Z"))
	private boolean forceNaturalSpawning(boolean original, @Local ChunkHolder chunkHolder) {
		return original || this.ticketStorage.port_lib$shouldForceNaturalSpawning(chunkHolder.getPos());
	}
}
