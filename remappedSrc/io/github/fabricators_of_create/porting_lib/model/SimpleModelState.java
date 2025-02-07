package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.collect.ImmutableMap;
import io.github.fabricators_of_create.porting_lib.extensions.ModelStateExtensions;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.AffineTransformation;

/**
 * Simple implementation of IModelState via a map and a default value.
 */
public final class SimpleModelState implements ModelBakeSettings, ModelStateExtensions {
	public static final SimpleModelState IDENTITY = new SimpleModelState(AffineTransformation.identity());

	private final ImmutableMap<?, AffineTransformation> map;
	private final AffineTransformation base;

	public SimpleModelState(ImmutableMap<?, AffineTransformation> map) {
		this(map, AffineTransformation.identity());
	}

	public SimpleModelState(AffineTransformation base) {
		this(ImmutableMap.of(), base);
	}

	public SimpleModelState(ImmutableMap<?, AffineTransformation> map, AffineTransformation base) {
		this.map = map;
		this.base = base;
	}

	@Override
	public AffineTransformation getRotation() {
		return base;
	}

	@Override
	public AffineTransformation getPartTransformation(Object part) {
		return map.getOrDefault(part, AffineTransformation.identity());
	}
}
