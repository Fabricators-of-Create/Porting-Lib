package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.math.Vector3f;

import io.github.fabricators_of_create.porting_lib.model.QuadTransformers;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FaceBakery.class)
public class FaceBakeryMixin {
	@Inject(method = "bakeQuad", at = @At("RETURN"))
	public void port_lib$bakeEmisssiveQuad(Vector3f posFrom, Vector3f posTo, BlockElementFace face, TextureAtlasSprite sprite, Direction facing, ModelState transform, BlockElementRotation partRotation, boolean shade, ResourceLocation modelLocation, CallbackInfoReturnable<BakedQuad> cir) {
		QuadTransformers.settingEmissivity(face.getEmissivity()).processInPlace(cir.getReturnValue());
	}
}
