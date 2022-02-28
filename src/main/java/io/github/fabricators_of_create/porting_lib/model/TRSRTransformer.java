package io.github.fabricators_of_create.porting_lib.model;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;

public class TRSRTransformer extends VertexTransformer {
	private final Transformation transform;

	public TRSRTransformer(IVertexConsumer parent, Transformation transform) {
		super(parent);
		this.transform = transform;
	}

	@Override
	public void put(int element, float... data) {
		switch (getVertexFormat().getElements().get(element).getUsage()) {
			case POSITION -> {
				Vector4f pos = new Vector4f(data[0], data[1], data[2], data[3]);
				((TransformationExtensions) (Object) transform).transformPosition(pos);
				data[0] = pos.x();
				data[1] = pos.y();
				data[2] = pos.z();
				data[3] = pos.w();
			}
			case NORMAL -> {
				Vector3f normal = new Vector3f(data[0], data[1], data[2]);
				((TransformationExtensions) (Object) transform).transformNormal(normal);
				data[0] = normal.x();
				data[1] = normal.y();
				data[2] = normal.z();
			}
		}
		super.put(element, data);
	}
}
