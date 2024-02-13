package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.ServerChunkCacheExtension;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;

import net.minecraft.server.level.TicketType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;

import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;

import net.minecraft.world.level.storage.LevelData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin implements ServerChunkCacheExtension {
	@Shadow
	@Final
	private DistanceManager distanceManager;

	@Override
	public <T> void addRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue, boolean forceTicks) {
		this.distanceManager.addRegionTicket(pType, pPos, pDistance, pValue, forceTicks);
	}

	@Override
	public <T> void removeRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue, boolean forceTicks) {
		this.distanceManager.removeRegionTicket(pType, pPos, pDistance, pValue, forceTicks);
	}

	@Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getPos()Lnet/minecraft/world/level/ChunkPos;"))
	private void checkForcedTicks(CallbackInfo ci, @Local(index = 14) LevelChunk levelChunk, @Share("force_ticks") LocalRef<Boolean> forceTicks) {
		forceTicks.set(this.distanceManager.shouldForceTicks(levelChunk.getPos().toLong()));
	}

	@ModifyExpressionValue(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;anyPlayerCloseEnoughForSpawning(Lnet/minecraft/world/level/ChunkPos;)Z"))
	private boolean shouldForce(boolean original, @Share("force_ticks") LocalRef<Boolean> forceTicks) {
		if (forceTicks.get())
			return true;
		return original;
	}

	@ModifyExpressionValue(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isNaturalSpawningAllowed(Lnet/minecraft/world/level/ChunkPos;)Z"))
	private boolean shouldForce2(boolean original, @Share("force_ticks") LocalRef<Boolean> forceTicks) {
		if (forceTicks.get())
			return true;
		return original;
	}
}
