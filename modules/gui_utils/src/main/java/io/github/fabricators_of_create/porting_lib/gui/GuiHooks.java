package io.github.fabricators_of_create.porting_lib.gui;

import com.mojang.datafixers.util.Either;

import io.github.fabricators_of_create.porting_lib.gui.events.GatherComponentsEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiHooks {
	public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
		return gatherTooltipComponents(stack, textElements, Optional.empty(), mouseX, screenWidth, screenHeight, fallbackFont);
	}

	public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
		Font font = fallbackFont;//getTooltipFont(stack, fallbackFont);
		List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
				.map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
				.collect(Collectors.toCollection(ArrayList::new));
		itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));

		var event = new GatherComponentsEvent(stack, screenWidth, screenHeight, elements, -1);
		event.sendEvent();
		if (event.isCanceled()) return List.of();

		// text wrapping
		int tooltipTextWidth = event.getTooltipElements().stream()
				.mapToInt(either -> either.map(font::width, component -> 0))
				.max()
				.orElse(0);

		boolean needsWrap = false;

		int tooltipX = mouseX + 12;
		if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
			tooltipX = mouseX - 16 - tooltipTextWidth;
			if (tooltipX < 4) // if the tooltip doesn't fit on the screen
			{
				if (mouseX > screenWidth / 2)
					tooltipTextWidth = mouseX - 12 - 8;
				else
					tooltipTextWidth = screenWidth - 16 - mouseX;
				needsWrap = true;
			}
		}

		if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth()) {
			tooltipTextWidth = event.getMaxWidth();
			needsWrap = true;
		}

		int tooltipTextWidthF = tooltipTextWidth;
		if (needsWrap) {
			return event.getTooltipElements().stream()
					.flatMap(either -> either.map(
							text -> splitLine(text, font, tooltipTextWidthF),
							component -> Stream.of(ClientTooltipComponent.create(component))
					))
					.toList();
		}
		return event.getTooltipElements().stream()
				.map(either -> either.map(
						text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
						ClientTooltipComponent::create
				))
				.toList();
	}

	private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
		if (text instanceof Component component && component.getString().isEmpty()) {
			return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
		}
		return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
	}

	public static final List<String> MODS_TO_WRAP = new ArrayList<>();

	public static void wrapModTooltips(String modid) {
		MODS_TO_WRAP.add(modid);
	}
}
