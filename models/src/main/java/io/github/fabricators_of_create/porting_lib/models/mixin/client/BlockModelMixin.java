package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.util.function.Function;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.models.RenderMaterialModel;
import io.github.fabricators_of_create.porting_lib.models.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.class)
public class BlockModelMixin implements BlockModelExtensions {
	private RenderMaterial port_lib$material;

	@Override
	public void port_lib$setRenderMaterial(RenderMaterial material) {
		this.port_lib$material = material;
	}

	@ModifyReturnValue(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("RETURN"))
	private BakedModel port_lib$wrapModel(BakedModel model, ModelBaker modelBaker, BlockModel blockModel, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation, boolean bl) {
		if (port_lib$material != null)
			return new RenderMaterialModel(model, port_lib$material);
		return model;
	}
}
