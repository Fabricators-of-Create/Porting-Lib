package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public interface IdentifiableResourceManagerReloadListener extends IdentifiableResourceReloadListener, ResourceManagerReloadListener {
}
