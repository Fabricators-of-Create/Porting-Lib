package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import java.lang.reflect.Type;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.api.renderer.v1.Renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.models.PortingLibModelLoadingRegistry;
import io.github.fabricators_of_create.porting_lib.models.util.RenderTypeUtil;
import io.github.fabricators_of_create.porting_lib.models.extensions.BlockModelExtensions;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
	@ModifyReturnValue(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"))
	private BlockModel addModelRenderType(BlockModel model, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject modelJson = jsonElement.getAsJsonObject();

		if (modelJson.has("render_material")) {
			JsonObject materialObj = GsonHelper.getAsJsonObject(modelJson, "render_material");
			RenderMaterial material = PortingLibModelLoadingRegistry.GSON.fromJson(materialObj, RenderMaterial.class);
			((BlockModelExtensions) model).port_lib$setRenderMaterial(material);
		} else if (modelJson.has("render_type")) {
			Renderer renderer = RendererAccess.INSTANCE.getRenderer();
			if (renderer != null) {
				String typeName = GsonHelper.getAsString(modelJson, "render_type");
				BlendMode blendMode = BlendMode.fromRenderLayer(RenderTypeUtil.get(ResourceLocation.parse(typeName)));
				((BlockModelExtensions) model).port_lib$setBlendMode(blendMode);
			}
		}

		return model;
	}
}
