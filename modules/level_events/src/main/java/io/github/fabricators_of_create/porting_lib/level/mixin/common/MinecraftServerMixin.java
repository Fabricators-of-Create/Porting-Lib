package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.level.LevelHooks;
import io.github.fabricators_of_create.porting_lib.level.events.LevelEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.storage.ServerLevelData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Shadow
	@Final
	private Map<ResourceKey<Level>, ServerLevel> levels;

	@Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;isInitialized()Z"))
	private void onLoadOverworld(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
		new LevelEvent.Load(this.levels.get(Level.OVERWORLD)).sendEvent();
	}

	@Inject(method = "createLevels", at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
			ordinal = 1,
			shift = At.Shift.AFTER
	))
	private void onLoadWorld(ChunkProgressListener chunkProgressListener, CallbackInfo ci, @Local(index = 18) ResourceKey<Level> key) {
		new LevelEvent.Load(levels.get(key)).sendEvent();
	}

	@Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;close()V"))
	private void onStopServer(CallbackInfo ci, @Local(index = 2) ServerLevel serverLevel) {
		new LevelEvent.Unload(serverLevel).sendEvent();
	}

	@Inject(method = "setInitialSpawn", at = @At(value = "NEW", target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/ChunkPos;"), cancellable = true)
	private static void onCreateWorldSpawn(ServerLevel serverLevel, ServerLevelData serverLevelData, boolean bl, boolean bl2, CallbackInfo ci) {
		if (LevelHooks.onCreateWorldSpawn(serverLevel, serverLevelData))
			ci.cancel();
	}
}
