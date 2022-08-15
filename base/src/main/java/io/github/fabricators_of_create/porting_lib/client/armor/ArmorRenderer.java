package io.github.fabricators_of_create.porting_lib.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface ArmorRenderer {

	/**
	 * Renders an armor part.
	 *
	 * @param matrices			the matrix stack
	 * @param vertexConsumers	the vertex consumer provider
	 * @param stack				the item stack of the armor item
	 * @param entity			the entity wearing the armor item
	 * @param slot				the equipment slot in which the armor stack is worn
	 * @param light				packed lightmap coordinates
	 * @param contextModel		the model provided by {@link RenderLayer#getParentModel()}
	 * @param armorModel		the original armor model
	 */
	void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel, HumanoidModel<LivingEntity> armorModel);
}
