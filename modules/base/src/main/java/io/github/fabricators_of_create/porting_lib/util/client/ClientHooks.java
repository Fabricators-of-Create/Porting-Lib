package io.github.fabricators_of_create.porting_lib.util.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.event.client.TextureAtlasStitchedEvent;
import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.ApiStatus;

public class ClientHooks {
	public static ResourceLocation getArmorTexture(Entity entity, ItemStack armor, ArmorMaterial.Layer layer, boolean innerModel, EquipmentSlot slot) {
		ResourceLocation result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, layer, innerModel);
		return result != null ? result : layer.texture(innerModel);
	}

	public static void setPartVisibility(HumanoidModel<?> armorModel, EquipmentSlot slot) {
		armorModel.setAllVisible(false);
		switch (slot) {
			case HEAD:
				armorModel.head.visible = true;
				armorModel.hat.visible = true;
				break;
			case CHEST:
				armorModel.body.visible = true;
				armorModel.rightArm.visible = true;
				armorModel.leftArm.visible = true;
				break;
			case LEGS:
				armorModel.body.visible = true;
				armorModel.rightLeg.visible = true;
				armorModel.leftLeg.visible = true;
				break;
			case FEET:
				armorModel.rightLeg.visible = true;
				armorModel.leftLeg.visible = true;
		}
	}

	@ApiStatus.Internal
	public static RenderType RENDER_TYPE = null;

	/**
	 * Sets the current {@link RenderType} to use for rendering a single block in {@link net.minecraft.client.renderer.block.BlockRenderDispatcher#renderSingleBlock(BlockState, PoseStack, MultiBufferSource, int, int)}.
	 * It is very important you set this to null after you have rendered your block(s).
	 *
	 * @param renderType The render type you want to render the block in. Ex: {@link RenderType#translucent()}
	 */
	public static void setRenderType(RenderType renderType) {
		RENDER_TYPE = renderType;
	}

	public static void onTextureAtlasStitched(TextureAtlas atlas) {
		new TextureAtlasStitchedEvent(atlas).sendEvent();
	}
}
