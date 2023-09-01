package io.github.fabricators_of_create.porting_lib.util.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ClientHooks {
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

	public static final List<String> MODS_TO_WRAP = new ArrayList<>();

	public static void wrapModTooltips(String modid) {
		MODS_TO_WRAP.add(modid);
	}

	public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font font) {
		List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
				.map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
				.collect(Collectors.toCollection(ArrayList::new));
		itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));

		// text wrapping
		int tooltipTextWidth = elements.stream()
				.mapToInt(either -> either.map(font::width, component -> 0))
				.max()
				.orElse(0);

		boolean needsWrap = false;

		int tooltipX = mouseX + 12;
		if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
			tooltipX = mouseX - 16 - tooltipTextWidth;
			if (tooltipX < 4) { // if the tooltip doesn't fit on the screen
				if (mouseX > screenWidth / 2)
					tooltipTextWidth = mouseX - 12 - 8;
				else
					tooltipTextWidth = screenWidth - 16 - mouseX;
				needsWrap = true;
			}
		}

		int tooltipTextWidthF = tooltipTextWidth;
		if (needsWrap) {
			return elements.stream()
					.flatMap(either -> either.map(
							text -> font.split(text, tooltipTextWidthF).stream().map(ClientTooltipComponent::create),
							component -> Stream.of(ClientTooltipComponent.create(component))
					))
					.toList();
		}
		return elements.stream()
				.map(either -> either.map(
						text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
						ClientTooltipComponent::create
				))
				.toList();
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
