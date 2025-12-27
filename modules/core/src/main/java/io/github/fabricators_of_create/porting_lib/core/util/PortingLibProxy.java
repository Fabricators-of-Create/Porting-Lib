package io.github.fabricators_of_create.porting_lib.core.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.Nullable;

public class PortingLibProxy {
	// I don't like this solution, but I'm too lazy to rewrite it
	public static final PortingLibProxy INSTANCE = instantiate();

	private static PortingLibProxy instantiate() {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> {
				try {
					yield (PortingLibProxy) Class.forName("io.github.fabricators_of_create.porting_lib.core.client.PortingLibClientProxy").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Failed to instantiate client proxy", e);
				}
			}
			case SERVER -> new PortingLibProxy();
		};
	}

	public BlockableEventLoop<Runnable> getClientExecutor() {
		throw new UnsupportedOperationException("Cannot access client on the server");
	}

	public TooltipFlag getTooltipFlag() {
		return TooltipFlag.NORMAL;
	}

	@Nullable
	public <T> HolderLookup.RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.registryAccess().lookup(key).orElse(null);
		}
		return null;
	}
}
