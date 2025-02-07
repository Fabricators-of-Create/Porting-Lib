package io.github.fabricators_of_create.porting_lib.model;

import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class TRSRTransformer extends VertexTransformer {
	private final AffineTransformation transform;

	public TRSRTransformer(IVertexConsumer parent, AffineTransformation transform) {
		super(parent);
		this.transform = transform;
	}

	@Override
	public void put(int element, float... data) {
		switch (getVertexFormat().getElements().get(element).getType()) {
			case POSITION -> {
				Vector4f pos = new Vector4f(data[0], data[1], data[2], data[3]);
				transform.transformPosition(pos);
				data[0] = pos.getX();
				data[1] = pos.getY();
				data[2] = pos.getZ();
				data[3] = pos.getW();
			}
			case NORMAL -> {
				Vec3f normal = new Vec3f(data[0], data[1], data[2]);
				transform.transformNormal(normal);
				data[0] = normal.getX();
				data[1] = normal.getY();
				data[2] = normal.getZ();
			}
		}
		super.put(element, data);
	}
}
