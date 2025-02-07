package io.github.fabricators_of_create.porting_lib.model;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class VertexTransformer implements IVertexConsumer {
	protected final IVertexConsumer parent;

	public VertexTransformer(IVertexConsumer parent) {
		this.parent = parent;
	}

	@Override
	public VertexFormat getVertexFormat() {
		return parent.getVertexFormat();
	}

	@Override
	public void setQuadTint(int tint) {
		parent.setQuadTint(tint);
	}

	@Override
	public void setTexture(Sprite texture) {
		parent.setTexture(texture);
	}

	@Override
	public void setQuadOrientation(Direction orientation) {
		parent.setQuadOrientation(orientation);
	}

	@Override
	public void setApplyDiffuseLighting(boolean diffuse) {
		parent.setApplyDiffuseLighting(diffuse);
	}

	@Override
	public void put(int element, float... data) {
		parent.put(element, data);
	}
}
