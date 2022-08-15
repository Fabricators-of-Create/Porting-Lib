package io.github.fabricators_of_create.porting_lib.mixin.client.transfer;

import io.github.fabricators_of_create.porting_lib.extensions.ClientLevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientBlockApiCache;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements ClientLevelExtensions {
	// lookup stuff, from FAPI

	@Unique
	private final Map<BlockPos, List<WeakReference<ClientBlockApiCache>>> port_lib$apiLookupCaches = new Object2ReferenceOpenHashMap<>();
	@Unique
	private int port_lib$apiLookupAccessesWithoutCleanup = 0;

	@Override
	public void port_lib$invalidateCache(BlockPos pos) {
		List<WeakReference<ClientBlockApiCache>> caches = port_lib$apiLookupCaches.get(pos);

		if (caches != null) {
			caches.removeIf(weakReference -> weakReference.get() == null);

			if (caches.size() == 0) {
				port_lib$apiLookupCaches.remove(pos);
			} else {
				caches.forEach(weakReference -> {
					ClientBlockApiCache cache = weakReference.get();

					if (cache != null) {
						cache.invalidate();
					}
				});
			}
		}

		port_lib$apiLookupAccessesWithoutCleanup++;

		// Try to invalidate GC'd lookups from the cache after 2 * the number of cached lookups
		if (port_lib$apiLookupAccessesWithoutCleanup > 2 * port_lib$apiLookupCaches.size()) {
			port_lib$apiLookupCaches.entrySet().removeIf(entry -> {
				entry.getValue().removeIf(weakReference -> weakReference.get() == null);
				return entry.getValue().isEmpty();
			});

			port_lib$apiLookupAccessesWithoutCleanup = 0;
		}
	}

	@Override
	public void port_lib$registerCache(BlockPos pos, ClientBlockApiCache cache) {
		List<WeakReference<ClientBlockApiCache>> caches = port_lib$apiLookupCaches.computeIfAbsent(pos.immutable(), ignored -> new ArrayList<>());
		caches.removeIf(weakReference -> weakReference.get() == null);
		caches.add(new WeakReference<>(cache));
		port_lib$apiLookupAccessesWithoutCleanup++;
	}
}
