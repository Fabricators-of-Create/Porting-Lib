package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public abstract class IdentifiableSimplePreparableReloadListener<T> extends SimplePreparableReloadListener<T> implements IdentifiableResourceReloadListener {
	private final ResourceLocation id;

	protected IdentifiableSimplePreparableReloadListener(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public ResourceLocation getFabricId() {
		return id;
	}
}
