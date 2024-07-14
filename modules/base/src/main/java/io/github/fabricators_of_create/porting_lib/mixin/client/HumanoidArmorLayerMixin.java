package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRenderer;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRendererRegistry;
import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;

import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {

	public HumanoidArmorLayerMixin(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderLayerParent) {
		super(renderLayerParent);
	}

//	@Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;Lnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V", ordinal = 2), cancellable = true)
//	public<T extends LivingEntity, A extends HumanoidModel<T>> void port_lib$fixArmorTextures(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
//		ItemStack itemStack = entity.getItemBySlot(armorSlot);
//		if(itemStack.getItem() instanceof ArmorTextureItem) {
//			ResourceLocation resourceLocation = ClientHooks.getArmorTexture(entity, itemStack, armorSlot, null);
//			VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
//					vertexConsumers, RenderType.armorCutoutNoCull(resourceLocation), false, itemStack.hasFoil()
//			);
//			model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//			ci.cancel();
//		}
//	} TODO: PORT

	@Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
	public<T extends LivingEntity, A extends HumanoidModel<T>> void port_lib$armorRegistry(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, EquipmentSlot armorSlot, int light, HumanoidModel<LivingEntity> armorModel, CallbackInfo ci) {
		ItemStack stack = entity.getItemBySlot(armorSlot);
		ArmorRenderer renderer = ArmorRendererRegistry.get(stack.getItem());

		if (renderer != null) {
			renderer.render(matrices, vertexConsumers, stack, entity, armorSlot, light, getParentModel(), armorModel);
			ci.cancel();
		}
	}
}
