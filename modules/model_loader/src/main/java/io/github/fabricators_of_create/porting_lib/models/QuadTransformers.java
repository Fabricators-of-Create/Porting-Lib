package io.github.fabricators_of_create.porting_lib.models;

import com.google.common.base.Preconditions;
import com.mojang.math.Transformation;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

/**
 * A collection of {@link net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform} implementations.
 *
 * @see net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform
 */
public final class QuadTransformers {

	private static final RenderContext.QuadTransform EMPTY = quad -> {
		return true;
	};

	private static final RenderContext.QuadTransform[] EMISSIVE_TRANSFORMERS = Util.make(new RenderContext.QuadTransform[16], array -> {
		Arrays.setAll(array, i -> applyingLightmap(LightTexture.pack(i, i)));
	});

	/**
	 * {@return a {@link MutableQuadView} transformer that does nothing}
	 */
	public static RenderContext.QuadTransform empty() {
		return EMPTY;
	}

	/**
	 * {@return a new {@link MutableQuadView} transformer that applies the specified {@link Transformation }}
	 */
	public static RenderContext.QuadTransform applying(Transformation transform) {
		if (transform.isIdentity())
			return empty();
		return quad -> {
			for (int i = 0; i < 4; i++) {
				float x = quad.x(i);
				float y = quad.y(i);
				float z = quad.z(i);

				Vector4f pos = new Vector4f(x, y, z, 1);
				transform.transformPosition(pos);
				pos.div(pos.w);

				quad.pos(i, pos.x(), pos.y(), pos.z());
			}

			for (int i = 0; i < 4; i++) {
				if (quad.hasNormal(i)) {
					float x = quad.normalX(i);
					float y = quad.normalY(i);
					float z = quad.normalZ(i);

					Vector3f pos = new Vector3f(x, y, z);
					transform.transformNormal(pos);

					quad.normal(i, pos);
				}
			}
			return true;
		};
	}

	/**
	 * @return A new {@link MutableQuadView} transformer that applies the specified packed light value.
	 */
	public static RenderContext.QuadTransform applyingLightmap(int packedLight) {
		return quad -> {
			for (int i = 0; i < 4; i++)
				quad.lightmap(i, packedLight);
			return true;
		};
	}

	/**
	 * @return A new {@link MutableQuadView} transformer that applies the specified block and sky light values.
	 */
	public static RenderContext.QuadTransform applyingLightmap(int blockLight, int skyLight) {
		return applyingLightmap(LightTexture.pack(blockLight, skyLight));
	}

	/**
	 * @return A {@link MutableQuadView} transformer that sets the lightmap to the given emissivity (0-15)
	 */
	public static RenderContext.QuadTransform settingEmissivity(int emissivity) {
		Preconditions.checkArgument(emissivity >= 0 && emissivity < 16, "Emissivity must be between 0 and 15.");
		return EMISSIVE_TRANSFORMERS[emissivity];
	}

	/**
	 * @return A {@link MutableQuadView} transformer that sets the lightmap to its max value
	 */
	public static RenderContext.QuadTransform settingMaxEmissivity() {
		return EMISSIVE_TRANSFORMERS[15];
	}

	/**
	 * @param color The color in ARGB format.
	 * @return A {@link MutableQuadView} transformer that sets the color to the specified value.
	 */
	public static RenderContext.QuadTransform applyingColor(int color) {
		final int fixedColor = toABGR(color);
		return quad -> {
			for (int i = 0; i < 4; i++)
				quad.spriteColor(i, 0, fixedColor);
			return true;
		};
	}

	/**
	 * This method supplies a default alpha value of 255 (no transparency)
	 * @param red The red value (0-255)
	 * @param green The green value (0-255)
	 * @param blue The blue value (0-255)
	 * @return A {@link MutableQuadView} transformer that sets the color to the specified value.
	 */
	public static RenderContext.QuadTransform applyingColor(int red, int green, int blue) {
		return applyingColor(255, red, green, blue);
	}

	/**
	 * @param alpha The alpha value (0-255)
	 * @param red The red value (0-255)
	 * @param green The green value (0-255)
	 * @param blue The blue value (0-255)
	 * @return A {@link MutableQuadView} transformer that sets the color to the specified value.
	 */
	public static RenderContext.QuadTransform applyingColor(int alpha, int red, int green, int blue) {
		return applyingColor(alpha << 24 | red << 16 | green << 8 | blue);
	}

	/**
	 * Converts an ARGB color to an ABGR color, as the commonly used color format is not the format colors end up packed into.
	 * This function doubles as its own inverse.
	 * @param color ARGB color
	 * @return ABGR color
	 */
	public static int toABGR(int color) {
		return (color & 0xFF00FF00) // alpha and green same spot
				| ((color >> 16) & 0x000000FF) // red moves to blue
				| ((color << 16) & 0x00FF0000); // blue moves to red
	}

	private QuadTransformers() {}
}
