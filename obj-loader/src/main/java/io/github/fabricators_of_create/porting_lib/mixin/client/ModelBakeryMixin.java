package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.model.obj.ObjLoader;
import io.github.fabricators_of_create.porting_lib.model.obj.ObjModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Shadow
	protected abstract void cacheAndQueueDependencies(ResourceLocation location, UnbakedModel model);

	@Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
	public void port_lib$loadObjModels(ResourceLocation modelLocation, CallbackInfo ci) {
		if(modelLocation.getPath().startsWith("block") || modelLocation.getPath().startsWith("item"))
			return;
		cacheAndQueueDependencies(modelLocation, ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(modelLocation, true, true, true, true, null)));
	}
}
