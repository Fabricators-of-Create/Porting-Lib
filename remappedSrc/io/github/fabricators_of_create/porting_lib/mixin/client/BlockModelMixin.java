package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import io.github.fabricators_of_create.porting_lib.PortingLib;
import io.github.fabricators_of_create.porting_lib.model.CompositeModelState;

import io.github.fabricators_of_create.porting_lib.model.PerspectiveMapWrapper;

import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.BlockModelExtensions;
import io.github.fabricators_of_create.porting_lib.model.BlockModelConfiguration;
import io.github.fabricators_of_create.porting_lib.model.IModelGeometry;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(JsonUnbakedModel.class)
public abstract class BlockModelMixin implements BlockModelExtensions {
	@Unique
	private final BlockModelConfiguration data = new BlockModelConfiguration((JsonUnbakedModel) (Object) this);

	@Shadow
	public String name;

	@Shadow
	@Final
	private List<ModelOverride> overrides;

	@Shadow
	public abstract JsonUnbakedModel getRootModel();

	@Shadow
	public abstract SpriteIdentifier getMaterial(String name);

	@Unique
	@Override
	public BlockModelConfiguration getGeometry() {
		return data;
	}

	@Unique
	@Override
	public ModelOverrideList getOverrides(ModelLoader pModelBakery, JsonUnbakedModel pModel, Function<SpriteIdentifier, Sprite> textureGetter) {
		return this.overrides.isEmpty() ? ModelOverrideList.EMPTY : new ModelOverrideList(pModelBakery, pModel, pModelBakery::getOrLoadModel/*, textureGetter*/, this.overrides);
	}

	@Inject(method = "getMaterials", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;getElements()Ljava/util/List;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$getModelMaterials(Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors, CallbackInfoReturnable<Collection<SpriteIdentifier>> cir, Set set, JsonUnbakedModel blockModel, Set<SpriteIdentifier> materials) {
		if(data.hasCustomGeometry()) {
			materials.addAll(data.getTextureDependencies(modelGetter, missingTextureErrors));
			this.overrides.forEach((p_111475_) -> {
				UnbakedModel unbakedmodel1 = modelGetter.apply(p_111475_.getModelId());
				if (!Objects.equals(unbakedmodel1, this)) {
					materials.addAll(unbakedmodel1.getTextureDependencies(modelGetter, missingTextureErrors));
				}
			});
			if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
				ItemModelGenerator.LAYERS.forEach((p_111467_) -> {
					materials.add(this.getMaterial(p_111467_));
				});
			}
			cir.setReturnValue(materials);
		}
	}

	@Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
	public void handleCustomModels(ModelLoader modelBakery, JsonUnbakedModel otherModel, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, Identifier modelLocation, boolean guiLight3d, CallbackInfoReturnable<BakedModel> cir) {
		JsonUnbakedModel blockModel = (JsonUnbakedModel) (Object) this;
		IModelGeometry<?> customModel = data.getCustomGeometry();
		ModelBakeSettings customModelState = data.getCustomModelState();
		ModelBakeSettings newModelState = modelTransform;
		if (customModelState != null)
			newModelState = new CompositeModelState(modelTransform, customModelState, modelTransform.isUvLocked());

		if (customModel != null) {
			BakedModel model = customModel.bake(blockModel.getGeometry(), modelBakery, spriteGetter, newModelState, blockModel.getOverrides(modelBakery, otherModel, spriteGetter), modelLocation);
			if (customModelState != null && !(model instanceof TransformTypeDependentItemBakedModel))
				model = new PerspectiveMapWrapper(model, customModelState);
			cir.setReturnValue(model);
		}
	}

	@Inject(method = "getElements", at = @At("HEAD"), cancellable = true)
	public void fixElements(CallbackInfoReturnable<List<ModelElement>> cir) {
		if (data.hasCustomGeometry()) cir.setReturnValue(java.util.Collections.emptyList());
	}


}
