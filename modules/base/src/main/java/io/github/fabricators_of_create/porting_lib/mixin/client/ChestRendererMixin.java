package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.util.MaterialChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

@Mixin(ChestRenderer.class)
public abstract class ChestRendererMixin<T extends BlockEntity & LidBlockEntity> {

	@Unique
	private Material port_lib$customMaterial = null;

	@Inject(
			method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Sheets;chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$customChestMaterial(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci, Level level, boolean bl, BlockState blockState, ChestType chestType) {
		if(this instanceof MaterialChest materialChest)
			port_lib$customMaterial = materialChest.getMaterial(blockEntity, chestType);
	}

	@ModifyReceiver(
			method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
	)
	public Material port_lib$customMaterial(Material old, MultiBufferSource buffer, Function<ResourceLocation, RenderType> renderTypeGetter) {
		return port_lib$customMaterial != null ? port_lib$customMaterial : old;
	}
}
