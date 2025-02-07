package io.github.fabricators_of_create.porting_lib.client.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

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
	 * @param contextModel		the model provided by {@link FeatureRenderer#getContextModel()}
	 * @param armorModel		the original armor model
	 */
	void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel, BipedEntityModel<LivingEntity> armorModel);
}
