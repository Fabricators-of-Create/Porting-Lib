package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.model.IModelGeometry;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.List;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {

  @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"), cancellable = true)
  public void modelLoading(JsonElement element, Type type, JsonDeserializationContext deserializationContext, CallbackInfoReturnable<BlockModel> cir) {
    BlockModel model = cir.getReturnValue();
    JsonObject jsonobject = element.getAsJsonObject();
    IModelGeometry<?> geometry = ModelLoaderRegistry.deserializeGeometry(deserializationContext, jsonobject);

	List<BlockElement> elements = model.getElements();
	if (geometry != null) {
		elements.clear();
		model.getGeometry().setCustomGeometry(geometry);
	}
  }
}
