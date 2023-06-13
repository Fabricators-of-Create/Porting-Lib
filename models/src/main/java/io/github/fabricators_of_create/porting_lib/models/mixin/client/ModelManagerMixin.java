package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.util.Map;

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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
	@Shadow
	private Map<ResourceLocation, BakedModel> bakedRegistry;

	@Inject(method = "loadModels", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=dispatch", shift = At.Shift.BEFORE))
	public void port_lib$onModifyBakingResult(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
		ModelEvents.MODIFY_BAKING_RESULT.invoker().onModifyBakingResult(modelBakery.getBakedTopLevelModels(), modelBakery);
	}

	@Inject(method = "apply", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=cache", shift = At.Shift.BEFORE))
	public void port_lib$onModelBake(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci) {
		ModelEvents.MODELS_BAKED.invoker().onModelsBaked((ModelManager) (Object) this, bakedRegistry, reloadState.modelBakery());
	}
}
