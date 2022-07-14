package io.github.fabricators_of_create.porting_lib.util.client;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockAndTintGetter;

import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.Nullable;

public class FluidAttributeRenderHandler implements FluidRenderHandler {
	protected final FluidAttributes attributes;
	private TextureAtlasSprite[] sprites;

	public FluidAttributeRenderHandler(FluidAttributes attributes) {
		this.attributes = attributes;
		this.sprites = new TextureAtlasSprite[attributes.getOverlayTexture() == null ? 2 : 3];
	}

	@Override
	public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return attributes.getColor(view, pos);
	}

	@Override
	public void reloadTextures(TextureAtlas textureAtlas) {
		sprites[0] = textureAtlas.getSprite(attributes.getStillTexture());
		sprites[1] = textureAtlas.getSprite(attributes.getFlowingTexture());

		if (attributes.getOverlayTexture() != null)
			sprites[3] = textureAtlas.getSprite(attributes.getOverlayTexture());
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return sprites;
	}
}
