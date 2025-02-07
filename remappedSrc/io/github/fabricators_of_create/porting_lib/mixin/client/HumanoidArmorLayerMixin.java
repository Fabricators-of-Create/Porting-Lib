package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRenderer;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRendererRegistry;
import io.github.fabricators_of_create.porting_lib.util.ArmorTextureItem;
import io.github.fabricators_of_create.porting_lib.util.ArmorTextureRegistry;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class HumanoidArmorLayerMixin extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {

	public HumanoidArmorLayerMixin(FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>> renderLayerParent) {
		super(renderLayerParent);
	}

	@Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
	private void port_lib$getArmorLocation(ArmorItem armorItem, boolean bl, String string, CallbackInfoReturnable<Identifier> cir) {
		Identifier id = ArmorTextureRegistry.get(armorItem.getMaterial());
		if (id != null) {
			cir.setReturnValue(id);
		}
	}

	@Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;ZLnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V", ordinal = 2), cancellable = true)
	public<T extends LivingEntity, A extends BipedEntityModel<T>> void port_lib$fixArmorTextures(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if(itemStack.getItem() instanceof ArmorTextureItem) {
			Identifier resourceLocation = ClientHooks.getArmorResource(entity, itemStack, armorSlot, null);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
					vertexConsumers, RenderLayer.getArmorCutoutNoCull(resourceLocation), false, itemStack.hasGlint()
			);
			model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			ci.cancel();
		}
	}

	@Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
	public<T extends LivingEntity, A extends BipedEntityModel<T>> void port_lib$armorRegistry(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, BipedEntityModel<LivingEntity> armorModel, CallbackInfo ci) {
		ItemStack stack = entity.getEquippedStack(armorSlot);
		ArmorRenderer renderer = ArmorRendererRegistry.get(stack.getItem());

		if (renderer != null) {
			renderer.render(matrices, vertexConsumers, stack, entity, armorSlot, light, getContextModel(), armorModel);
			ci.cancel();
		}
	}
}
