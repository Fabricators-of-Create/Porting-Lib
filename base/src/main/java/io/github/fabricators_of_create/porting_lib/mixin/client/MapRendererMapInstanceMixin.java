package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.fabricators_of_create.porting_lib.render.MapDecorationIterator;

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

	@Unique public AtomicInteger porting_Lib$cached_index = new AtomicInteger(0);

	@ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", shift = At.Shift.BY, by = 2))
	private Iterator<MapDecoration> porting_Lib$wrapIterator(Iterator<MapDecoration> iterator){
		return new MapDecorationIterator(iterator, porting_Lib$cached_index);
	}

	//---------------------------------------------------

	@Inject(method = "draw", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void porting_Lib$getCurrentIndex(PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, CallbackInfo ci, int i, int j, float f, Matrix4f matrix4f, VertexConsumer vertexConsumer, int k, Iterator<?> var11){
		this.porting_Lib$cached_index.set(k);
	}

	@ModifyVariable(method = "draw", at = @At(value = "LOAD", ordinal = 0), index = 10)
	private int porting_Lib$updateK(int old_k){
		return porting_Lib$cached_index.get();
	}
}
