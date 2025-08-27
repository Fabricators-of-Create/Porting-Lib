package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.injections.ModelBakerInjection;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(targets = "net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl")
public abstract class ModelBakery$ModelBakerImplMixin implements ModelBakerInjection {
	@Shadow
	@Final
	private ModelBakery field_40571;

	@Shadow
	@Final
	private Function<Material, TextureAtlasSprite> modelTextureGetter;

	@Override
	public UnbakedModel port_lib$getTopLevelModel(ModelResourceLocation location) {
		return ((ModelBakeryAccessor) field_40571).getTopLevelModels().get(location);
	}

	@Override
	public Function<Material, TextureAtlasSprite> port_lib$getModelTextureGetter() {
		return modelTextureGetter;
	}
}
