package io.github.fabricators_of_create.porting_lib.models.geometry;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

/**
 * Simple implementation of {@link ModelState}.
 */
public final class SimpleModelState implements ModelState {
	private final Transformation transformation;
	private final boolean uvLocked;

	public SimpleModelState(Transformation transformation, boolean uvLocked) {
		this.transformation = transformation;
		this.uvLocked = uvLocked;
	}

	public SimpleModelState(Transformation transformation) {
		this(transformation, false);
	}

	@Override
	public Transformation getRotation() {
		return transformation;
	}

	@Override
	public boolean isUvLocked() {
		return uvLocked;
	}
}

