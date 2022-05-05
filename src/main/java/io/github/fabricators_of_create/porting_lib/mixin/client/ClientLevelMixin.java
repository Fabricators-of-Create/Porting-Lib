package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.ClientLevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientBlockApiCache;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ClientLevelExtensions {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$init(CallbackInfo ci) {
		ClientWorldEvents.LOAD.invoker().onWorldLoad(minecraft, MixinHelper.cast(this));
	}

	// lookup stuff, from FAPI

	@Unique
	private final Map<BlockPos, List<WeakReference<ClientBlockApiCache>>> apiLookupCaches = new Object2ReferenceOpenHashMap<>();
	@Unique
	private int apiLookupAccessesWithoutCleanup = 0;

	@Override
	public void port_lib$invalidateCache(BlockPos pos) {
		List<WeakReference<ClientBlockApiCache>> caches = apiLookupCaches.get(pos);

		if (caches != null) {
			caches.removeIf(weakReference -> weakReference.get() == null);

			if (caches.size() == 0) {
				apiLookupCaches.remove(pos);
			} else {
				caches.forEach(weakReference -> {
					ClientBlockApiCache cache = weakReference.get();

					if (cache != null) {
						cache.invalidate();
					}
				});
			}
		}

		apiLookupAccessesWithoutCleanup++;

		// Try to invalidate GC'd lookups from the cache after 2 * the number of cached lookups
		if (apiLookupAccessesWithoutCleanup > 2 * apiLookupCaches.size()) {
			apiLookupCaches.entrySet().removeIf(entry -> {
				entry.getValue().removeIf(weakReference -> weakReference.get() == null);
				return entry.getValue().isEmpty();
			});

			apiLookupAccessesWithoutCleanup = 0;
		}
	}

	@Override
	public void port_lib$registerCache(BlockPos pos, ClientBlockApiCache cache) {
		List<WeakReference<ClientBlockApiCache>> caches = apiLookupCaches.computeIfAbsent(pos.immutable(), ignored -> new ArrayList<>());
		caches.removeIf(weakReference -> weakReference.get() == null);
		caches.add(new WeakReference<>(cache));
		apiLookupAccessesWithoutCleanup++;
	}
}
