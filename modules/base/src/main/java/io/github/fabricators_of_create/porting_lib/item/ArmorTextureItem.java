package io.github.fabricators_of_create.porting_lib.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * An Armor Item with custom logic for getting the texture.
 */
public interface ArmorTextureItem {
	/**
	 * Called by {@link HumanoidArmorLayer#renderArmorPiece(PoseStack, MultiBufferSource, LivingEntity, EquipmentSlot, int, HumanoidModel)} to determine the armor texture that
	 * should be use for the currently equipped item. This will only be called on
	 * instances of ItemArmor.
	 *
	 * Returning null from this function will use the default value.
	 *
	 * @param stack      ItemStack for the equipped armor
	 * @param entity     The entity wearing the armor
	 * @param slot       The slot the armor is in
	 * @param layer      The armor layer
	 * @param innerModel Whether the inner model is used
	 * @return Path of texture to bind, or null to use default
	 */
	@Nullable
	default ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return null;
	}
}
