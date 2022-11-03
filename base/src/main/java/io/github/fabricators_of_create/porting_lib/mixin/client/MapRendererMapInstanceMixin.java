package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMapInstanceMixin {
	@Unique
	public Iterator<?> porting_Lib$cachedIterator = null;
	@Unique public boolean porting_Lib$iteratorIsEmpty = false;

	@Unique private boolean porting_Lib$updateIndexFromCache = false;
	@Unique public int porting_Lib$cached_index = 0;

	@Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;getDecorations()Ljava/lang/Iterable;", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void porting_Lib$getLoopIteratorAndIndex(PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, CallbackInfo ci, int i, int j, float f, Matrix4f matrix4f, VertexConsumer vertexConsumer, int k, Iterator<?> var11){
		this.porting_Lib$cachedIterator = var11;
		this.porting_Lib$cached_index = k;
	}

	@ModifyVariable(method = "draw", at = @At(value = "STORE"))
	private MapDecoration porting_Lib$gatherNextValueIfCustomRender(MapDecoration value){
		while(value.render(porting_Lib$cached_index)) {
			this.porting_Lib$cached_index++;
			this.porting_Lib$updateIndexFromCache = true;

			if (this.porting_Lib$cachedIterator.hasNext()) {
				value = (MapDecoration) porting_Lib$cachedIterator.next();
			} else {
				this.porting_Lib$iteratorIsEmpty = true;

				//Should this be the last value that was gathered incase someone injects between somehow?
				return null;
			}
		}

		return value;
	}

	@Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration;renderOnFrame()Z"), cancellable = true)
	public void porting_Lib$cancelIfIteratorIsEmpty(PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, CallbackInfo ci){
		if(porting_Lib$iteratorIsEmpty) ci.cancel();
	}

	@ModifyVariable(method = "draw", at = @At(value = "LOAD", ordinal = 0), index = 10)
	private int porting_Lib$updateKIfNeeded(int old_k){
		if(porting_Lib$updateIndexFromCache){
			this.porting_Lib$updateIndexFromCache = false;

			return porting_Lib$cached_index;
		}

		return old_k;
	}
}
