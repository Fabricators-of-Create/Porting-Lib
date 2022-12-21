package io.github.fabricators_of_create.porting_lib.model_loader.mixin.client;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.UnbakedGeometryHelper;

import net.minecraft.client.resources.model.ModelBaker;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.model_loader.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin implements BlockModelExtensions {
	@Unique
	private final BlockGeometryBakingContext data = new BlockGeometryBakingContext((BlockModel) (Object) this);

	@Shadow
	public String name;

	@Shadow
	@Final
	private List<ItemOverride> overrides;

	@Shadow
	public abstract BlockModel getRootModel();

	@Shadow
	public abstract Material getMaterial(String name);

	@Unique
	@Override
	public BlockGeometryBakingContext getGeometry() {
		return data;
	}

	@Unique
	@Override
	public ItemOverrides getOverrides(ModelBaker pModelBaker, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(pModelBaker, pModel/*, textureGetter*/, this.overrides);
	}

	@Inject(method = "resolveParents", at = @At(value = "JUMP", opcode = Opcodes.IFNULL))
	public void port_lib$getModelMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, CallbackInfo ci) {
		if(data.hasCustomGeometry()) {
			data.getCustomGeometry().resolveParents(modelGetter, data);
		}
	}

	@Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	public void handleCustomModels(ModelBaker modelBaker, BlockModel otherModel, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation, boolean guiLight3d, CallbackInfoReturnable<BakedModel> cir) {
		BlockModel blockModel = (BlockModel) (Object) this;

		if (data.getCustomGeometry() != null) {
			cir.setReturnValue(UnbakedGeometryHelper.bake(blockModel, modelBaker, otherModel, spriteGetter, modelTransform, modelLocation, guiLight3d));
		}
	}

	@Inject(method = "getElements", at = @At("HEAD"), cancellable = true)
	public void fixElements(CallbackInfoReturnable<List<BlockElement>> cir) {
		if (data.hasCustomGeometry()) cir.setReturnValue(java.util.Collections.emptyList());
	}
}
