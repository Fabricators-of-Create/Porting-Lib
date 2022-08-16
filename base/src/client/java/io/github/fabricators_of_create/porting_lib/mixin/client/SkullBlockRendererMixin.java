package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.event.client.CreateSkullModelsCallback;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
	@Inject(method = "createSkullRenderers", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 5), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void port_lib$createSkullModels(EntityModelSet entityModelSet, CallbackInfoReturnable<Map<SkullBlock.Type, SkullModelBase>> cir, ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder) {
		CreateSkullModelsCallback.EVENT.invoker().onSkullModelsCreated(builder, entityModelSet);
	}
}
