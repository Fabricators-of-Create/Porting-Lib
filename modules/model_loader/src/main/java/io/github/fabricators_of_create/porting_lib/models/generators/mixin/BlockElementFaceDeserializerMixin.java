package io.github.fabricators_of_create.porting_lib.models.generators.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.renderer.block.model.BlockElementFace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(BlockElementFace.Deserializer.class)
public class BlockElementFaceDeserializerMixin {
	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElementFace;", at = @At("TAIL"))
	private void port_lib$addRenderMaterial(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<BlockElementFace> cir) {
		JsonObject json = jsonElement.getAsJsonObject();
		if (json.has("render_material")) {
//			((BlockElementFaceExtensions)cir.getReturnValue()).port_lib$setRenderMaterial(PortingLibModelLoadingRegistry.GSON.fromJson(json.get("render_material"), RenderMaterial.class));
		}
	}
}
