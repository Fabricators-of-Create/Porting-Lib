package io.github.fabricators_of_create.porting_lib.models;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

import java.util.List;
import java.util.function.Function;

public class ExtendedItemOverrides extends ItemOverrides {
	// I'm honestly not sure why this is pass here, forge doesn't document why it does this at all and vanilla already uses a TextureGetter
	public ExtendedItemOverrides(ModelBaker modelBaker, BlockModel blockModel, List<ItemOverride> list, Function<Material, TextureAtlasSprite> spriteGetter) {
		super(modelBaker, blockModel, list);
	}
}
