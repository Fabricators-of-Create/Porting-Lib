package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.util.function.Function;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.models.CustomBlendModeModel;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.models.RenderMaterialModel;
import io.github.fabricators_of_create.porting_lib.models.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.class)
public class BlockModelMixin implements BlockModelExtensions {
	@Unique
	private RenderMaterial material;
	@Unique
	private BlendMode blendMode;

	@Override
	public void port_lib$setRenderMaterial(RenderMaterial material) {
		this.material = material;
	}

	@Override
	public void port_lib$setBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	// use a WrapMethod to make sure we catch any injected cancels too (ex. the model_loader module)
	@WrapMethod(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;")
	private BakedModel useCustomRenderMaterial(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation id, boolean bl, Operation<BakedModel> original) {
		BakedModel baked = original.call(baker, model, spriteGetter, state, id, bl);

		if (this.material != null) {
			return new RenderMaterialModel(baked, this.material);
		} else if (this.blendMode != null) {
			return new CustomBlendModeModel(baked, this.blendMode);
		} else {
			return baked;
		}
	}
}
