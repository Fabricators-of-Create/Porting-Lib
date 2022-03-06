package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Same as {@link ResourceManagerReloadListener}, but only runs if the mod loader state is valid, used as client resource listeners can cause a misleading crash report if something else throws
 * Fabric note - no :)
 */
public interface ISafeManagerReloadListener extends ResourceManagerReloadListener {
	@Override
	default void onResourceManagerReload(ResourceManager resourceManager) {
//    if (ModLoader.isLoadingStateValid()) {
		onReloadSafe(resourceManager);
//    }
	}

	/**
	 * Safely handle a resource manager reload. Only runs if the mod loading state is valid
	 * @param resourceManager  Resource manager
	 */
	void onReloadSafe(ResourceManager resourceManager);
}
