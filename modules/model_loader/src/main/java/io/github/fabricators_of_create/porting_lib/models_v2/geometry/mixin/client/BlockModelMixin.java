package io.github.fabricators_of_create.porting_lib.models_v2.geometry.mixin.client;

import java.util.List;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.models_v2.geometry.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.models_v2.geometry.VisibilityData;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.models_v2.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models_v2.geometry.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

@Mixin(BlockModel.class)
public class BlockModelMixin implements BlockModelExtensions {
	@Shadow
	@Final
	private List<ItemOverride> overrides;
	@Shadow
	@Nullable
	public BlockModel parent;
	@Unique
	private IUnbakedGeometry<?> customModel;
	@Unique
	@Nullable
	private Transformation rootTransform;
	@Unique
	private final VisibilityData visibilityData = new VisibilityData();
	private BlockGeometryBakingContext pl$customData;

	@Inject(
			method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
			at = @At("HEAD"),
			cancellable = true
	)
	public void handleCustomModels(ModelBaker modelBaker, BlockModel ownerModel, Function<Material, TextureAtlasSprite> spriteGetter,
								   ModelState modelTransform, ResourceLocation modelLocation, boolean guiLight3d, CallbackInfoReturnable<BakedModel> cir) {
		IUnbakedGeometry<?> geometry = getCustomGeometry();
		if (geometry != null) {
			ItemOverrides overrides = getOverrides(modelBaker, ownerModel, spriteGetter);
			cir.setReturnValue(geometry.bake(
					(BlockModel) (Object) this, modelBaker, spriteGetter, modelTransform, overrides, modelLocation, guiLight3d
			));
		}
	}

	@Inject(method = "resolveParents", at = @At("HEAD"))
	private void handleCustomResolveParents(Function<ResourceLocation, UnbakedModel> function, CallbackInfo ci) {
		if (getCustomGeometry() != null)
			getCustomGeometry().resolveParents(function, pl$self());
	}

	@Override
	public ItemOverrides getPortingLibOverrides(ModelBaker p_250138_, BlockModel p_251800_, Function<Material, TextureAtlasSprite> spriteGetter) {
		return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(p_250138_, p_251800_, this.overrides/*, spriteGetter*/);
	}

	@Override
	public void setPortingLibCustomGeometry(IUnbakedGeometry<?> geometry) {
		this.customModel = geometry;
	}

	@Override
	public IUnbakedGeometry<?> getPortingLibCustomGeometry() {
		return this.parent != null && customModel == null ? this.parent.getCustomGeometry() : customModel;
	}

	@Override
	public VisibilityData getPortingLibVisibilityData() {
		return this.visibilityData;
	}

	@Override
	public boolean isPortingLibComponentVisible(String part, boolean fallback) {
		return pl$self().parent != null && !visibilityData.hasCustomVisibility(part) ?
				pl$self().parent.isPortingLibComponentVisible(part, fallback) :
				visibilityData.isVisible(part, fallback);
	}

	@Override
	public Transformation getPortingLibRootTransform() {
		if (rootTransform != null)
			return rootTransform;
		return pl$self().parent != null ? pl$self().parent.getPortingLibRootTransform() : Transformation.identity();
	}

	public void setPortingLibRootTransform(Transformation rootTransform) {
		this.rootTransform = rootTransform;
	}

	@Override
	public BlockGeometryBakingContext getPortingLibCustomData() {
		return this.pl$customData;
	}

	private BlockModel pl$self() {
		return (BlockModel) (Object) this;
	}
}
