package io.github.fabricators_of_create.porting_lib.model;

import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Assumes that the data length is not less than e.getElements().size().
 * Also assumes that element index passed will increment from 0 to format.getElements().size() - 1.
 * Normal, Color and UV are assumed to be in 0-1 range.
 */
public interface IVertexConsumer {
	/**
	 * @return the format that should be used for passed data.
	 */
	VertexFormat getVertexFormat();

	void setQuadTint(int tint);

	void setQuadOrientation(Direction orientation);

	void setApplyDiffuseLighting(boolean diffuse);

	void setTexture(Sprite texture);

	void put(int element, float... data);
}
