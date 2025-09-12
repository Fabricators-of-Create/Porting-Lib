package io.github.fabricators_of_create.porting_lib.gui.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import io.github.fabricators_of_create.porting_lib.gui.map.MapDecorationRendererManager;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMapInstanceMixin {

	@Shadow
	private MapItemSavedData data;
	@Shadow
	@Final
	private MapRenderer field_2047;

	// Instead of replacing the iterator just modifyexpression the renderOnFrame call
	@ModifyExpressionValue(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration;renderOnFrame()Z"))
	private boolean renderMapDecorationType(boolean original, PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, @Local MapDecoration mapDecoration, @Local(ordinal = 2)LocalIntRef indexRef) {
		if (original) {
			if (MapDecorationRendererManager.render(mapDecoration, poseStack, bufferSource, this.data, this.field_2047.decorationTextures, active, packedLight, indexRef.get())) {
				indexRef.set(indexRef.get() + 1);
				return false;
			}
		}
		return original;
	}
}
