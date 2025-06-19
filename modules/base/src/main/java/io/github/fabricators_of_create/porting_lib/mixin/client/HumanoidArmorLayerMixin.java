package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRenderer;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRendererRegistry;

import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.minecraft.world.item.ArmorMaterial;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
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

	@WrapOperation(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArmorMaterial$Layer;texture(Z)Lnet/minecraft/resources/ResourceLocation;"))
	public<T extends LivingEntity, A extends HumanoidModel<T>> ResourceLocation fixArmorTextures(ArmorMaterial.Layer instance, boolean innerTexture, Operation<ResourceLocation> original, @Local(argsOnly = true) T entity, @Local(argsOnly = true) EquipmentSlot slot, @Local(ordinal = 0) ItemStack stack) {
		return ClientHooks.getArmorTexture(entity, stack, instance, innerTexture, slot, original);
	}

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
