package io.github.fabricators_of_create.porting_lib.models.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Vertex pipeline element that applies a transformation to incoming geometry.
 */
public class TransformingVertexPipeline extends VertexConsumerWrapper {
	private final Transformation transformation;

	public TransformingVertexPipeline(VertexConsumer parent, Transformation transformation) {
		super(parent);
		this.transformation = transformation;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		var vec = new Vector4f(x, y, z, 1);
		transformation.transformPosition(vec);
		vec.div(vec.w);
		return super.addVertex(vec.x(), vec.y(), vec.z());
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		var vec = new Vector3f(x, y, z);
		transformation.transformNormal(vec);
		vec.normalize();
		return super.setNormal(vec.x(), vec.y(), vec.z());
	}
}
