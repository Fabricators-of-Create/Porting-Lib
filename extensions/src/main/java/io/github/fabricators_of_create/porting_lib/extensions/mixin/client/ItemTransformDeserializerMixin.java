package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemTransformExtensions;
import net.minecraft.client.renderer.block.model.ItemTransform;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(ItemTransform.Deserializer.class)
public abstract class ItemTransformDeserializerMixin {
	@Shadow
	protected abstract Vector3f getVector3f(JsonObject jsonObject, String string, Vector3f vector3f);

	@Shadow
	@Final
	private static Vector3f DEFAULT_ROTATION;

	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/ItemTransform;", at = @At("RETURN"))
	private void port_lib$rightRotation(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ItemTransform> cir) {
		Vector3f rightRotation = this.getVector3f(jsonElement.getAsJsonObject(), "right_rotation", DEFAULT_ROTATION);
		// why is this cast required???
		((ItemTransformExtensions)cir.getReturnValue()).setRightRotation(rightRotation);
	}
}
