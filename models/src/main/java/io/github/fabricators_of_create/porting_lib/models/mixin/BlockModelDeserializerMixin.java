package io.github.fabricators_of_create.porting_lib.models.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.models.RenderTypeUtil;
import io.github.fabricators_of_create.porting_lib.models.extensions.BlockModelExtensions;
import net.minecraft.client.renderer.block.model.BlockModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"))
	private void port_lib$addModelRenderType(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<BlockModel> cir) {
		JsonObject modelJson = jsonElement.getAsJsonObject();
		if (modelJson.has("render_type")) {
			var renderTypeHintName = GsonHelper.getAsString(modelJson, "render_type");
			((BlockModelExtensions)cir.getReturnValue()).port_lib$setRenderType(RenderTypeUtil.get(new ResourceLocation(renderTypeHintName)));
		}
	}
}
