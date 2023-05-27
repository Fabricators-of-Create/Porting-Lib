package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.io.InputStreamReader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import io.github.fabricators_of_create.porting_lib.models.obj.ObjLoader;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
//	@Shadow
//	protected abstract void cacheAndQueueDependencies(ResourceLocation location, UnbakedModel model);
//
//	@Inject(method = "loadModel", at = @At("HEAD"), cancellable = true) TODO: Re-add support for model loading
//	public void port_lib$loadObjModels(ResourceLocation modelLocation, CallbackInfo ci) {
//		if (!modelLocation.getPath().contains(".json"))
//			return;
//		Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath())).ifPresent(resource -> {
//			try {
//				JsonObject jsonObject = Streams.parse(new JsonReader(new InputStreamReader(resource.open(), Charsets.UTF_8))).getAsJsonObject();
//				if (jsonObject.has(PortingLib.ID + ":" + "obj_marker")) {
//					ResourceLocation objLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
//					ObjModel model = ObjLoader.INSTANCE.loadModel(Minecraft.getInstance().getResourceManager().getResource(objLocation).orElseThrow(), new ObjModel.ModelSettings(objLocation, true, true, true, true, null));
//					if (model != null) {
//						cacheAndQueueDependencies(modelLocation, model);
//						ci.cancel();
//					}
//				}
//			} catch (Exception e) {
//
//			}
//		});
//	}
}
