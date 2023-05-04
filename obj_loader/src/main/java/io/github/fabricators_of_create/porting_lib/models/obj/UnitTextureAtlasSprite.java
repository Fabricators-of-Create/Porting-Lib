package io.github.fabricators_of_create.porting_lib.models.obj;

import com.mojang.blaze3d.platform.NativeImage;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;

/**
 * A helper sprite with UVs spanning the entire texture.
 * <p>
 * Useful for baking quads that won't be used with an atlas.
 */
public class UnitTextureAtlasSprite extends TextureAtlasSprite {
	public static final ResourceLocation LOCATION = PortingConstants.id("unit");
	public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

	private UnitTextureAtlasSprite() {
		super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false), AnimationMetadataSection.EMPTY), 1, 1, 0, 0);
	}

	@Override
	public float getU(double u) {
		return (float) u / 16;
	}

	@Override
	public float getV(double v) {
		return (float) v / 16;
	}
}
