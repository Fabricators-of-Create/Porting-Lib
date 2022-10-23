package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.client.ModelsBakedCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

@Environment(EnvType.CLIENT)
@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
	@Shadow
	public Map<ResourceLocation, BakedModel> bakedRegistry;

	@Inject(method = "apply", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=cache", shift = At.Shift.BEFORE))
	public void port_lib$onModelBake(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci) {
		ModelsBakedCallback.EVENT.invoker().onModelsBaked((ModelManager) (Object) this, bakedRegistry, reloadState.modelBakery());
	}
}
