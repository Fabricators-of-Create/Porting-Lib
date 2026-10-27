package io.github.fabricators_of_create.porting_lib.gui.mixin;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.core.util.IteratorWrapper;
import io.github.fabricators_of_create.porting_lib.gui.map.MapDecorationRendererManager;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMapInstanceMixin {

	@Shadow private MapItemSavedData data;
	@Shadow @Final MapRenderer field_2047;

	@Definition(id = "data", field = "Lnet/minecraft/client/gui/MapRenderer$MapInstance;data:Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;")
	@Definition(id = "getDecorations", method = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;getDecorations()Ljava/lang/Iterable;")
	@Definition(id = "iterator", method = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;")
	@Expression("this.data.getDecorations().iterator()")
	@ModifyExpressionValue(method = "draw", at = @At("MIXINEXTRAS:EXPRESSION"))
	private <T extends MapDecoration> Iterator<T> renderMapDecorationType(Iterator<T> original, PoseStack poseStack, MultiBufferSource bufferSource, boolean active, int packedLight, @Share("indexIncrement") LocalIntRef indexRef) {
		return new IteratorWrapper<>(original, decoration -> {
			if (MapDecorationRendererManager.render(decoration, poseStack, bufferSource, data, field_2047.decorationTextures, active, packedLight, indexRef.get())) {
				indexRef.set(indexRef.get() + 1);
				return null;
			}

			return decoration;
		});
	}

	@Inject(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
	private void preIncrementIndex(CallbackInfo ci, @Share("indexIncrement") LocalIntRef indexIncrementRef, @Local(ordinal = 2) LocalIntRef indexRef) {
		// MixinExtras doesn't allow @Local's LocalRefs to stay alive for longer than their scope.
		// So instead, we are doing this to avoid causing a crash.
		indexRef.set(indexRef.get() + indexIncrementRef.get());
		indexIncrementRef.set(0);
	}
}
