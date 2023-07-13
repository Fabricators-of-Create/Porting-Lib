package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.models.events.ModelEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.AtlasSet.StitchResult;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelManager.ReloadState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
	@Shadow
	private Map<ResourceLocation, BakedModel> bakedRegistry;

	@ModifyExpressionValue(
			method = "loadModels",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;"
			)
	)
	public Map<ResourceLocation, BakedModel> port_lib$onModifyBakingResult(Map<ResourceLocation, BakedModel> models,
																		   ProfilerFiller profiler,
																		   Map<ResourceLocation, StitchResult> atlases,
																		   ModelBakery bakery) {
		ModelEvents.MODIFY_BAKING_RESULT.invoker().onModifyBakingResult(models, bakery);
		return models;
	}

	@Inject(
			method = "apply",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"
			)
	)
	public void port_lib$onModelBake(ReloadState state, ProfilerFiller profiler, CallbackInfo ci) {
		ModelEvents.MODELS_BAKED.invoker().onModelsBaked((ModelManager) (Object) this, bakedRegistry, state.modelBakery());
	}
}
