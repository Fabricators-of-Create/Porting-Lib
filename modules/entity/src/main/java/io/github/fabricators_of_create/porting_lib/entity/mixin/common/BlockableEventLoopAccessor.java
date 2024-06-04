package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.thread.BlockableEventLoop;

@Mixin(BlockableEventLoop.class)
public interface BlockableEventLoopAccessor {
	@Invoker
	CompletableFuture<Void> callSubmitAsync(Runnable task);
}
