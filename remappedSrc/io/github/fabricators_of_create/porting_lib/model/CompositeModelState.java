package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.base.Objects;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.AffineTransformation;

/**
 * An {@link ModelBakeSettings} that combines the transforms from two child {@link ModelBakeSettings}.
 */
public class CompositeModelState implements ModelBakeSettings {
	private final ModelBakeSettings first;
	private final ModelBakeSettings second;
	private final boolean uvLock;

	public CompositeModelState(ModelBakeSettings first, ModelBakeSettings second) {
		this(first, second, false);
	}

	public CompositeModelState(ModelBakeSettings first, ModelBakeSettings second, boolean uvLock) {
		this.first = first;
		this.second = second;
		this.uvLock = uvLock;
	}

	@Override
	public boolean isUvLocked() {
		return uvLock;
	}

	@Override
	public AffineTransformation getRotation() {
		return first.getRotation().multiply(second.getRotation());
	}

	@Override
	public AffineTransformation getPartTransformation(Object part) {
		return first.getPartTransformation(part).compose(second.getPartTransformation(part));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CompositeModelState that = (CompositeModelState) o;
		return Objects.equal(first, that.first) && Objects.equal(second, that.second);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(first, second);
	}
}
