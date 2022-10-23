package io.github.fabricators_of_create.porting_lib.model_loader.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraft.client.renderer.block.model.BakedQuad;

public class LightUtil {
	private LightUtil() {
	}

	public static float diffuseLight(float x, float y, float z) {
		return Math.min(x * x * 0.6f + y * y * ((3f + y) / 4f) + z * z * 0.8f, 1f);
	}

	public static int getLightOffset(int v) {
		return (v * 8) + 6;
	}

	public static void setLightData(BakedQuad q, int light) {
		int[] data = q.getVertices();
		for (int i = 0; i < 4; i++) {
			data[getLightOffset(i)] = light;
		}
	}

	public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
		VertexFormatElement element = formatTo.getElements().get(e);
		int vertexStart = v * formatTo.getVertexSize() + formatTo.getOffset(e);
		int count = element.getCount();
		VertexFormatElement.Type type = element.getType();
		int size = type.getSize();
		int mask = (256 << (8 * (size - 1))) - 1;
		for (int i = 0; i < 4; i++) {
			if (i < count) {
				int pos = vertexStart + size * i;
				int index = pos >> 2;
				int offset = pos & 3;
				int bits = 0;
				float f = i < from.length ? from[i] : 0;
				if (type == VertexFormatElement.Type.FLOAT) {
					bits = Float.floatToRawIntBits(f);
				} else if (
						type == VertexFormatElement.Type.UBYTE ||
								type == VertexFormatElement.Type.USHORT ||
								type == VertexFormatElement.Type.UINT
				) {
					bits = Math.round(f * mask);
				} else {
					bits = Math.round(f * (mask >> 1));
				}
				to[index] &= ~(mask << (offset * 8));
				to[index] |= (((bits & mask) << (offset * 8)));
				// TODO handle overflow into to[index + 1]
			}
		}
	}
}
