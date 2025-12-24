package io.github.fabricators_of_create.porting_lib.item.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.fabricators_of_create.porting_lib.item.client.callbacks.RegisterItemDecorationsEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ItemDecoratorHandler {
	private final List<IItemDecorator> itemDecorators;

	private static Map<Item, ItemDecoratorHandler> DECORATOR_LOOKUP = ImmutableMap.of();

	private static final ItemDecoratorHandler EMPTY = new ItemDecoratorHandler();

	private ItemDecoratorHandler() {
		this.itemDecorators = ImmutableList.of();
	}

	private ItemDecoratorHandler(List<IItemDecorator> itemDecorators) {
		this.itemDecorators = ImmutableList.copyOf(itemDecorators);
	}

	public static void init() {
		var decorators = new HashMap<Item, List<IItemDecorator>>();
		var event = new RegisterItemDecorationsEvent(decorators);
		event.sendEvent();
		var builder = new ImmutableMap.Builder<Item, ItemDecoratorHandler>();
		decorators.forEach((item, itemDecorators) -> builder.put(item, new ItemDecoratorHandler(itemDecorators)));
		DECORATOR_LOOKUP = builder.build();
	}

	public static ItemDecoratorHandler of(ItemStack stack) {
		return DECORATOR_LOOKUP.getOrDefault(stack.getItem(), EMPTY);
	}

	public void render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		if (itemDecorators.isEmpty()) {
			return;
		}

		for (IItemDecorator itemDecorator : itemDecorators) {
			itemDecorator.render(guiGraphics, font, stack, xOffset, yOffset);
		}
	}
}
