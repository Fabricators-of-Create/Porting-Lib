package io.github.fabricators_of_create.porting_lib.mixin.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.client.ModelLoadCallback;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"))
	public void onModelLoad(BlockColors colors, ProfilerFiller profiler, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources, CallbackInfo ci) {
		ModelLoadCallback.EVENT.invoker().onModelsStartLoading(colors, profiler, modelResources, blockStateResources);
	}
}
