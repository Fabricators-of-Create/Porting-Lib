package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.util.ArmorTextureRegistry;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.util.ArmorTextureItem;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

import javax.annotation.Nullable;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {

	@Shadow
	@Final
	private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;

	@Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
	private void port_lib$getArmorLocation(ArmorItem armorItem, boolean bl, String string, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation id = ArmorTextureRegistry.get(armorItem.getMaterial());
		if (id != null) {
			cir.setReturnValue(id);
		}
	}

	@Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ArmorItem;ZLnet/minecraft/client/model/HumanoidModel;ZFFFLjava/lang/String;)V", ordinal = 2), cancellable = true)
	public<T extends LivingEntity, A extends HumanoidModel<T>> void port_lib$fixArmorTextures(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
		ItemStack itemStack = entity.getItemBySlot(armorSlot);
		if(itemStack.getItem() instanceof ArmorTextureItem) {
			ResourceLocation resourceLocation = port_lib$getArmorResource(entity, itemStack, armorSlot, null);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
					vertexConsumers, RenderType.armorCutoutNoCull(resourceLocation), false, itemStack.hasFoil()
			);
			model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			ci.cancel();
		}
	}

	@Unique
	private ResourceLocation port_lib$getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
		if (stack.getItem() instanceof ArmorTextureItem armorTextureItem) {
			String s1 = armorTextureItem.getArmorTexture(stack, entity, slot, type);
			ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

			if (resourcelocation == null) {
				resourcelocation = new ResourceLocation(s1);
				ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
			}

			return resourcelocation;
		}
		throw new RuntimeException("port_lib$getArmorResource should only be called for ArmorTextureItems.");
	}
}
