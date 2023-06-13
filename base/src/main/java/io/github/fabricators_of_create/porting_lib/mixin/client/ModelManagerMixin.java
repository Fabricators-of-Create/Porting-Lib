package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.event.client.ModelsBakedCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.ModelManager;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
	@ModifyExpressionValue(
			method = "apply(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;"
			)
	)
	public Map<ResourceLocation, BakedModel> fireModelBakeEvent(Map<ResourceLocation, BakedModel> models,
									 ModelBakery bakery, ResourceManager resourceManager, ProfilerFiller profiler) {
		ModelsBakedCallback.EVENT.invoker().onModelsBaked((ModelManager) (Object) this, models, bakery);
		return models;
	}
}
