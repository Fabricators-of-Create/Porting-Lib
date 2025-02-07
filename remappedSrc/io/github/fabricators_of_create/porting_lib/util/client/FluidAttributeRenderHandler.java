package io.github.fabricators_of_create.porting_lib.util.client;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@SuppressWarnings("removal")
public class FluidAttributeRenderHandler implements FluidRenderHandler {
	protected final FluidAttributes attributes;
	private Sprite[] sprites;

	public FluidAttributeRenderHandler(FluidAttributes attributes) {
		this.attributes = attributes;
		this.sprites = new Sprite[attributes.getOverlayTexture() == null ? 2 : 3];
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return attributes.getColor(view, pos);
	}

	@Override
	public void reloadTextures(SpriteAtlasTexture textureAtlas) {
		sprites[0] = textureAtlas.getSprite(attributes.getStillTexture());
		sprites[1] = textureAtlas.getSprite(attributes.getFlowingTexture());

		if (attributes.getOverlayTexture() != null)
			sprites[2] = textureAtlas.getSprite(attributes.getOverlayTexture());
	}

	@Override
	public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return sprites;
	}
}
