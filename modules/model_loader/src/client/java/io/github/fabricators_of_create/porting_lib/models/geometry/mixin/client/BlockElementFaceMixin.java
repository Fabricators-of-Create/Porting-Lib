package io.github.fabricators_of_create.porting_lib.models.geometry.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import io.github.fabricators_of_create.porting_lib.models.geometry.extensions.BlockElementFaceExtension;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(BlockElementFace.class)
public class BlockElementFaceMixin implements BlockElementFaceExtension {
	private @Nullable ExtraFaceData port_lib$faceData;
	private MutableObject<BlockElement> port_lib$parent = new MutableObject<>();

	@Override
	public void port_lib$setFaceData(@Nullable ExtraFaceData faceData, MutableObject<BlockElement> parent) {
		this.port_lib$faceData = faceData;
		this.port_lib$parent = parent;
	}

	@Override
	public ExtraFaceData port_lib$faceData() {
		if(this.port_lib$faceData != null) {
			return this.port_lib$faceData;
		} else if(this.port_lib$parent.getValue() != null) {
			return this.port_lib$parent.getValue().port_lib$getFaceData();
		}
		return ExtraFaceData.DEFAULT;
	}

	@Mixin(BlockElementFace.Deserializer.class)
	public static class DeserializerMixin {
		@ModifyReturnValue(
				method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElementFace;",
				at = @At("RETURN")
		)
		private BlockElementFace addExtraData(BlockElementFace original, @Local(ordinal = 0) JsonObject jsonObject) {
			var faceData = ExtraFaceData.read(jsonObject.get("porting_lib_data"), null);
			original.port_lib$setFaceData(faceData, new MutableObject<>());
			return original;
		}
	}
}
