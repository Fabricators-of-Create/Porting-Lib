package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Preconditions;

import io.github.fabricators_of_create.porting_lib.models.injections.ModelManagerInjection;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.models.events.client.ModelEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin implements ModelManagerInjection {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	private Map<ModelResourceLocation, BakedModel> bakedRegistry;

	@Unique
	private ModelBakery port_lib$modelBakery;

	@Definition(id = "popPush", method = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
	@Definition(id = "profilerFiller", local = @Local(type = ProfilerFiller.class, argsOnly = true))
	@Expression("profilerFiller.popPush('dispatch')")
	@Inject(method = "loadModels", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void modifyBakingResult(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
		profilerFiller.popPush("porting_lib_modify_baking_result");

		Function<Material, TextureAtlasSprite> textureGetter = material -> {
			AtlasSet.StitchResult stitchResult = atlasPreparations.get(material.atlasLocation());
			TextureAtlasSprite sprite = stitchResult.getSprite(material.texture());
			if (sprite != null) {
				return sprite;
			}
			LOGGER.warn("Failed to retrieve texture '{}' from atlas '{}'", material.texture(), material.atlasLocation(), new Throwable());
			return stitchResult.missing();
		};

		(new ModelEvent.ModifyBakingResult(modelBakery.getBakedTopLevelModels(), textureGetter, modelBakery)).sendEvent();
	}

	@Definition(id = "profiler", local = @Local(type = ProfilerFiller.class, argsOnly = true))
	@Definition(id = "popPush", method = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V")
	@Expression("profiler.popPush('cache')")
	@Inject(method = "apply", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void callBakingCompletedEvent(ModelManager.ReloadState reloadState, ProfilerFiller profiler, CallbackInfo ci, @Local ModelBakery modelBakery) {
		port_lib$modelBakery = modelBakery;
		(new ModelEvent.BakingCompleted((ModelManager) (Object) this, this.bakedRegistry, modelBakery)).sendEvent();
	}

	@Override
	public ModelBakery port_lib$getModelBakery() {
		return Preconditions.checkNotNull(port_lib$modelBakery, "Attempted to query model bakery before it has been initialized.");
	}
}
