package io.github.fabricators_of_create.porting_lib.models.geometry.mixin.client;

import java.util.List;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.models.ExtendedItemOverrides;
import io.github.fabricators_of_create.porting_lib.models.geometry.BlockGeometryBakingContext;

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

import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.extensions.BlockModelExtensions;
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
	private final BlockGeometryBakingContext port_lib$customData = new BlockGeometryBakingContext((BlockModel) (Object) this);

	@Override
	public BlockGeometryBakingContext port_lib$getCustomData() {
		return port_lib$customData;
	}

	@Inject(
			method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Z)Lnet/minecraft/client/resources/model/BakedModel;",
			at = @At("HEAD"),
			cancellable = true
	)
	public void handleCustomModels(ModelBaker modelBaker, BlockModel owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, boolean guiLight3d, CallbackInfoReturnable<BakedModel> cir) {
		if (port_lib$customData.hasCustomGeometry()) {
			cir.setReturnValue(port_lib$customData.getCustomGeometry().bake(
					port_lib$customData, modelBaker, spriteGetter, modelState, getOverrides(modelBaker, owner, spriteGetter)
			));
		}
	}

	@Inject(method = "resolveParents", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
	private void handleCustomResolveParents(Function<ResourceLocation, UnbakedModel> function, CallbackInfo ci) {
		if (port_lib$customData.hasCustomGeometry())
			port_lib$customData.getCustomGeometry().resolveParents(function, port_lib$customData);
	}

	@Override
	public ItemOverrides getOverrides(ModelBaker bakery, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter) {
		return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ExtendedItemOverrides(bakery, model, this.overrides, spriteGetter);
	}
}
