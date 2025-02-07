package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;

public abstract class IdentifiableSimplePreparableReloadListener<T> extends SinglePreparationResourceReloader<T> implements IdentifiableResourceReloadListener {
	private final Identifier id;

	protected IdentifiableSimplePreparableReloadListener(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}
}
