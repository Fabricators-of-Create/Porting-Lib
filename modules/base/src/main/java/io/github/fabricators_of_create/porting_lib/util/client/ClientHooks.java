package io.github.fabricators_of_create.porting_lib.util.client;

import com.google.common.collect.Maps;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;

import io.github.fabricators_of_create.porting_lib.gui.GuiHooks;
import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientHooks {
	public static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

	public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
		String result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, type);
		return result != null ? result : _default;
	}

	/**
	 * More generic version of the above function, it allows for Items to have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack ItemStack for the armor
	 * @param slot Slot ID that the item is in
	 * @param type Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	public static ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
		ArmorItem item = (ArmorItem)stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

		s1 = getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

	public static void setPartVisibility(HumanoidModel<?> armorModel, EquipmentSlot slot) {
		armorModel.setAllVisible(false);
		switch(slot) {
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

	/**
	 * Moved to gui_utils module
	 */
	@Deprecated(forRemoval = true)
	public static void wrapModTooltips(String modid) {
		GuiHooks.wrapModTooltips(modid);
	}

	/**
	 * Moved to gui_utils module
	 */
	@Deprecated(forRemoval = true)
	public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font font) {
		return GuiHooks.gatherTooltipComponents(stack, textElements, itemComponent, mouseX, screenWidth, screenHeight, font);
	}

	@ApiStatus.Internal
	public static RenderType RENDER_TYPE = null;

	/**
	 * Sets the current {@link RenderType} to use for rendering a single block in {@link net.minecraft.client.renderer.block.BlockRenderDispatcher#renderSingleBlock(BlockState, PoseStack, MultiBufferSource, int, int)}.
	 * It is very important you set this to null after you have rendered your block(s).
	 * @param renderType The render type you want to render the block in. Ex: {@link RenderType#translucent()}
	 */
	public static void setRenderType(RenderType renderType) {
		RENDER_TYPE = renderType;
	}
}
