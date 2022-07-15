package io.github.fabricators_of_create.porting_lib.util.client;

import net.minecraft.client.Minecraft;

import net.minecraft.world.inventory.InventoryMenu;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

@SuppressWarnings("removal")
public class FluidAttributeRenderHandler implements FluidRenderHandler {
	protected final FluidAttributes attributes;
	private boolean invalidate = false;
	private TextureAtlasSprite[] sprites;
	private TextureAtlas atlas;

	public FluidAttributeRenderHandler(FluidAttributes attributes) {
		this.attributes = attributes;
		this.atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
		sprites = new TextureAtlasSprite[attributes.getOverlayTexture() == null ? 2 : 3];
		sprites[0] = atlas.getSprite(attributes.getStillTexture());
		sprites[1] = atlas.getSprite(attributes.getFlowingTexture());

		if (attributes.getOverlayTexture() != null)
			sprites[3] = atlas.getSprite(attributes.getOverlayTexture());
	}

	@Override
	public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return attributes.getColor(view, pos);
	}

	@Override
	public void reloadTextures(TextureAtlas textureAtlas) {
		this.atlas = textureAtlas;
		this.invalidate = true;
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		if (invalidate) {
			sprites[0] = atlas.getSprite(attributes.getStillTexture());
			sprites[1] = atlas.getSprite(attributes.getFlowingTexture());

			if (attributes.getOverlayTexture() != null)
				sprites[3] = atlas.getSprite(attributes.getOverlayTexture());
			invalidate = false;
		}
		return sprites;
	}
}
