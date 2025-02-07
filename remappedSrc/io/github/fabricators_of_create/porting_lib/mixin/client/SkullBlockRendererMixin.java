package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.event.client.CreateSkullModelsCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;

@Mixin(SkullBlockEntityRenderer.class)
public class SkullBlockRendererMixin {
	@Inject(method = "createSkullRenderers", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 5), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void port_lib$createSkullModels(EntityModelLoader entityModelSet, CallbackInfoReturnable<Map<SkullBlock.SkullType, SkullBlockEntityModel>> cir, ImmutableMap.Builder<SkullBlock.SkullType, SkullBlockEntityModel> builder) {
		CreateSkullModelsCallback.EVENT.invoker().onSkullModelsCreated(builder, entityModelSet);
	}
}
