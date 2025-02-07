package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.collect.ImmutableList;
import io.github.fabricators_of_create.porting_lib.util.LightUtil;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Allows easier building of BakedQuad objects. During building, data is stored
 * unpacked as floats, but is packed into the typical int array format on build.
 */
public class BakedQuadBuilder implements IVertexConsumer {
	private static final int SIZE = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getElements().size();

	private final float[][][] unpackedData = new float[4][SIZE][4];
	private final float eps = 1f / 0x100;
	private int tint = -1;
	private Direction orientation;
	private Sprite texture;
	private boolean applyDiffuseLighting = true;
	private int vertices = 0;
	private int elements = 0;
	private boolean full = false;
	private boolean contractUVs = false;

	public BakedQuadBuilder() {
	}

	public BakedQuadBuilder(Sprite texture) {
		this.texture = texture;
	}

	public void setContractUVs(boolean value) {
		this.contractUVs = value;
	}

	@Override
	public VertexFormat getVertexFormat() {
		return VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
	}

	@Override
	public void setQuadTint(int tint) {
		this.tint = tint;
	}

	@Override
	public void setQuadOrientation(Direction orientation) {
		this.orientation = orientation;
	}

	@Override
	public void setTexture(Sprite texture) {
		this.texture = texture;
	}

	@Override
	public void setApplyDiffuseLighting(boolean diffuse) {
		this.applyDiffuseLighting = diffuse;
	}

	@Override
	public void put(int element, float... data) {
		for (int i = 0; i < 4; i++) {
			if (i < data.length) {
				unpackedData[vertices][element][i] = data[i];
			} else {
				unpackedData[vertices][element][i] = 0;
			}
		}
		elements++;
		if (elements == SIZE) {
			vertices++;
			elements = 0;
		}
		if (vertices == 4) {
			full = true;
		}
	}

	public BakedQuad build() {
		if (!full) {
			throw new IllegalStateException("not enough data");
		}
		if (texture == null) {
			throw new IllegalStateException("texture not set");
		}
		if (contractUVs) {
			float tX = texture.getWidth() / (texture.getMaxU() - texture.getMinU());
			float tY = texture.getHeight() / (texture.getMaxV() - texture.getMinV());
			float tS = tX > tY ? tX : tY;
			float ep = 1f / (tS * 0x100);
			int uve = 0;
			ImmutableList<VertexFormatElement> elements = VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getElements();
			while (uve < elements.size()) {
				VertexFormatElement e = elements.get(uve);
				if (e.getType() == VertexFormatElement.Type.UV && e.getTextureIndex() == 0) {
					break;
				}
				uve++;
			}
			if (uve == elements.size()) {
				throw new IllegalStateException("Can't contract UVs: format doesn't contain UVs");
			}
			float[] uvc = new float[4];
			for (int v = 0; v < 4; v++) {
				for (int i = 0; i < 4; i++) {
					uvc[i] += unpackedData[v][uve][i] / 4;
				}
			}
			for (int v = 0; v < 4; v++) {
				for (int i = 0; i < 4; i++) {
					float uo = unpackedData[v][uve][i];
					float un = uo * (1 - eps) + uvc[i] * eps;
					float ud = uo - un;
					float aud = ud;
					if (aud < 0) aud = -aud;
					if (aud < ep) // not moving a fraction of a pixel
					{
						float udc = uo - uvc[i];
						if (udc < 0) udc = -udc;
						if (udc < 2 * ep) // center is closer than 2 fractions of a pixel, don't move too close
						{
							un = (uo + uvc[i]) / 2;
						} else // move at least by a fraction
						{
							un = uo + (ud < 0 ? ep : -ep);
						}
					}
					unpackedData[v][uve][i] = un;
				}
			}
		}
		int[] packed = new int[VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger() * 4];
		for (int v = 0; v < 4; v++) {
			for (int e = 0; e < SIZE; e++) {
				LightUtil.pack(unpackedData[v][e], packed, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, v, e);
			}
		}
		return new BakedQuad(packed, tint, orientation, texture, applyDiffuseLighting);
	}
}
