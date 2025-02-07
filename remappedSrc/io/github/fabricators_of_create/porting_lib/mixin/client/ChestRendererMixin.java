package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import io.github.fabricators_of_create.porting_lib.util.MaterialChest;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestRendererMixin<T extends BlockEntity & ChestAnimationProgress> {

	@Unique
	private SpriteIdentifier port_lib$customMaterial = null;

	@Inject(
			method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$customChestMaterial(BlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay, CallbackInfo ci, World level, boolean bl, BlockState blockState, ChestType chestType) {
		if(this instanceof MaterialChest materialChest)
			port_lib$customMaterial = materialChest.getMaterial(blockEntity, chestType);
	}

	@ModifyReceiver(
			method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
	)
	public SpriteIdentifier port_lib$customMaterial(SpriteIdentifier old, VertexConsumerProvider buffer, Function<Identifier, RenderLayer> renderTypeGetter) {
		return port_lib$customMaterial != null ? port_lib$customMaterial : old;
	}
}
