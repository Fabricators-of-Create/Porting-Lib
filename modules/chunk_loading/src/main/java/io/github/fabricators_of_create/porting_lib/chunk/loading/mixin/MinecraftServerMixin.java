package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ForcedChunksSavedData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@ModifyExpressionValue(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongIterator;hasNext()Z"))
	private boolean reinstatePersistentChunks(boolean original, @Local(index = 6) ServerLevel serverLevel2, @Local(index = 7) ForcedChunksSavedData forcedChunksSavedData) {
		if (!original) // a bit of a hack honestly but avoids us having to make a custom Injection Point
			PortingLibChunkManager.reinstatePersistentChunks(serverLevel2, forcedChunksSavedData);
		return original;
	}
}
