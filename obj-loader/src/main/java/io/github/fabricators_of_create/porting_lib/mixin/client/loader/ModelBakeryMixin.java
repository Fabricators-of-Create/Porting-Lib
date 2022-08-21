package io.github.fabricators_of_create.porting_lib.mixin.client.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.minecraft.server.packs.resources.ResourceManager;

import net.minecraft.util.GsonHelper;

import org.spongepowered.asm.mixin.Final;
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

import java.io.InputStreamReader;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Shadow
	protected abstract void cacheAndQueueDependencies(ResourceLocation location, UnbakedModel model);

	@Shadow
	@Final
	private ResourceManager resourceManager;

	@Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
	public void port_lib$loadObjModels(ResourceLocation modelLocation, CallbackInfo ci) {
		if (!modelLocation.getPath().contains(".json"))
			return;
		resourceManager.getResource(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath())).ifPresent(resource -> {
			try {
				JsonObject jsonObject = Streams.parse(new JsonReader(new InputStreamReader(resource.open(), Charsets.UTF_8))).getAsJsonObject();
				if (jsonObject.has(PortingConstants.ID + ":" + "obj_marker")) {
					ObjModel model = ObjLoader.INSTANCE.loadModel(resource, new ObjModel.ModelSettings(new ResourceLocation(GsonHelper.getAsString(jsonObject, "model")), true, true, true, true, null));
					if (model != null) {
						cacheAndQueueDependencies(modelLocation, model);
						ci.cancel();
					}
				}
			} catch (Exception e) {

			}
		});


	}
}
