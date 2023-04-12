package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.fabricators_of_create.porting_lib.render.MapDecorationIterator;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

@Mixin(value = MapRenderer.MapInstance.class, priority = 860) // apply earlier due to fragile local capture
public abstract class MapRendererMapInstanceMixin {

	@Unique
	private final AtomicInteger port_Lib$cached_index = new AtomicInteger(0);

	@ModifyVariable(
			method = "draw",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
					shift = At.Shift.BY,
					by = 2
			)
	)
	private Iterator<MapDecoration> port_Lib$wrapIterator(Iterator<MapDecoration> iterator) {
		return new MapDecorationIterator(iterator, port_Lib$cached_index);
	}

	@Inject(
			method = "draw",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z",
					shift = At.Shift.BY,
					by = 3
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void port_Lib$getCurrentIndex(PoseStack poseStack, MultiBufferSource bufferSource, boolean active,
										  int packedLight, CallbackInfo ci, int i, int j, float f, Matrix4f matrix4f,
										  VertexConsumer vertexConsumer, int k, Iterator<?> var11) {
		this.port_Lib$cached_index.set(k);
	}

	@ModifyVariable(method = "draw", at = @At(value = "LOAD", ordinal = 0), index = 10)
	private int port_Lib$updateK(int oldK) {
		return port_Lib$cached_index.get();
	}
}
