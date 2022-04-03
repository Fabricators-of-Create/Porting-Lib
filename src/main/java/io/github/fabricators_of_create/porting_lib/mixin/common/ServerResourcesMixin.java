package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.server.ReloadableServerResources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.event.AddReloadListenersCallback;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ServerResourcesMixin {

	@ModifyArgs(method = "loadResources",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
	private static void port_lib$DataPackRegistries(Args args) {
		List<PreparableReloadListener> listeners = new ArrayList<>(args.get(1));
		AddReloadListenersCallback.EVENT.invoker().addReloadListeners(listeners);
		args.set(1, listeners);
	}
}
