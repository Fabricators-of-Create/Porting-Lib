package io.github.fabricators_of_create.porting_lib.util.client;

import com.mojang.blaze3d.platform.NativeImage;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite.Info;

/**
 * A helper that lets you bake quads that won't be used with an atlas.
 */
public class UnitSprite extends TextureAtlasSprite {
	public static final UnitSprite INSTANCE = new UnitSprite();
	public static final ResourceLocation LOCATION = new ResourceLocation(PortingLib.ID, "unit");
	public static final Function<Material, TextureAtlasSprite> GETTER = (x) -> INSTANCE;

	private UnitSprite() {
		super(new TextureAtlas(LOCATION),
				new Info(LOCATION, 1, 1, AnimationMetadataSection.EMPTY),
				0, 1, 1,
				0, 0, new NativeImage(1, 1, false));
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
