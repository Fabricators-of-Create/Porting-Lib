package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public abstract class IdentifiableISafeManagerReloadListener implements ISafeManagerReloadListener, IdentifiableResourceReloadListener {

	private final ResourceLocation id;

	protected IdentifiableISafeManagerReloadListener(ResourceLocation id) {this.id = id;}

	@Override
	public ResourceLocation getFabricId() {
		return id;
	}
}
