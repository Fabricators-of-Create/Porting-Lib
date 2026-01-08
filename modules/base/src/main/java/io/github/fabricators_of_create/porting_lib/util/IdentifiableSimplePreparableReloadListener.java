package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public abstract class IdentifiableSimplePreparableReloadListener<T> extends SimplePreparableReloadListener<T> implements IdentifiableResourceReloadListener {
	private final Identifier id;

	protected IdentifiableSimplePreparableReloadListener(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}
}
