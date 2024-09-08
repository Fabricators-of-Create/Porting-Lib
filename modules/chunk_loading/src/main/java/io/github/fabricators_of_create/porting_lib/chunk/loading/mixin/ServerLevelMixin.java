package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	// Future me see if this can be replaced with a modify expression
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongSet;isEmpty()Z"))
	private boolean hasChunks(LongSet instance) {
		return ForcedChunkManager.hasForcedChunks((ServerLevel) (Object) this);
	}
}
