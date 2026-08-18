package io.github.fabricators_of_create.porting_lib.models.geometry.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import io.github.fabricators_of_create.porting_lib.models.QuadTransformers;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;

@Mixin(FaceBakery.class)
public abstract class FaceBakeryMixin {
	@ModifyReturnValue(method = "bakeQuad", at = @At("RETURN"))
	private BakedQuad applyQuadTransformations(BakedQuad original, @Local(argsOnly = true) BlockElementFace face) {
		ExtraFaceData data = face.port_lib$faceData();

		// TODO: AO
		if (!ExtraFaceData.DEFAULT.equals(data)) {
			QuadTransformers.applyingLightmap(data.blockLight(), data.skyLight()).processInPlace(original);
			QuadTransformers.applyingColor(data.color()).processInPlace(original);
		}

		return original;
	}
}
