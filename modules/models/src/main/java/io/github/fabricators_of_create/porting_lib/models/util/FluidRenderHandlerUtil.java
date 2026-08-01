package io.github.fabricators_of_create.porting_lib.models.util;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class FluidRenderHandlerUtil {
	public static @Nullable TextureAtlasSprite getSprite(FluidVariant fluid) {
		if (FluidVariantRendering.getHandler(fluid.getFluid()) != null) {
			return FluidVariantRendering.getSprite(fluid);
		} else if (FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid()) != null) {
			TextureAtlasSprite[] sprites = FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid()).getFluidSprites(null, null, fluid.getFluid().defaultFluidState());
			if (sprites != null && sprites[0] != null) {
				return sprites[0];
			}
		}

		return null;
	}

	public static int getColor(FluidVariant fluid) {
		if (FluidVariantRendering.getHandler(fluid.getFluid()) != null) {
			return FluidVariantRendering.getColor(fluid) | 0xFF000000;
		} else if (FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid()) != null) {
			return FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid())
					.getFluidColor(null, null, fluid.getFluid().defaultFluidState()) | 0xFF000000;
		}

		return 0xFFFFFFFF;
	}
}
