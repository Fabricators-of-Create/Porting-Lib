package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;

import net.minecraft.client.resources.model.ModelState;

/**
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelState implements ModelState {
	public static final SimpleModelState IDENTITY = new SimpleModelState(Transformation.identity());

	private final ImmutableMap<?, Transformation> map;
	private final Transformation base;

	public SimpleModelState(ImmutableMap<?, Transformation> map) {
		this(map, Transformation.identity());
	}

	public SimpleModelState(Transformation base) {
		this(ImmutableMap.of(), base);
	}

	public SimpleModelState(ImmutableMap<?, Transformation> map, Transformation base) {
		this.map = map;
		this.base = base;
	}

	@Override
	public Transformation getRotation() {
		return base;
	}

	//  @Override
	public Transformation getPartTransformation(Object part) {
		return map.getOrDefault(part, Transformation.identity());
	}
}
