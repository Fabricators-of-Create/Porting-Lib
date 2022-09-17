package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.block.model.BlockElementFace;

import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(BlockElementFace.Deserializer.class)
public class BlockElementFaceDeserializerMixin {
	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElementFace;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$emissivity(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockElementFace> cir, JsonObject jsonObject, Direction direction, int i, String string, BlockFaceUV blockFaceUV) {
		int emissivity = GsonHelper.getAsInt(jsonObject, "emissivity", 0);
		if (emissivity != net.minecraft.util.Mth.clamp(emissivity, 0, 15))
			throw new JsonParseException("The emissivity value must be between 0 and 15. Found: " + emissivity);
		cir.getReturnValue().setEmissivity(emissivity);
	}
}
