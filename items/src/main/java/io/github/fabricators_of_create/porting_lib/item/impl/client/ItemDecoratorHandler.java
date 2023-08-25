package io.github.fabricators_of_create.porting_lib.item.impl.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.fabricators_of_create.porting_lib.item.api.client.IItemDecorator;
import io.github.fabricators_of_create.porting_lib.item.api.client.callbacks.ItemDecorationsCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			var decorators = new HashMap<Item, List<IItemDecorator>>();
			ItemDecorationsCallback.EVENT.invoker().registerDecorators(decorators);
			var builder = new ImmutableMap.Builder<Item, ItemDecoratorHandler>();
			decorators.forEach((item, itemDecorators) -> builder.put(item, new ItemDecoratorHandler(itemDecorators)));
			DECORATOR_LOOKUP = builder.build();
		});
	}

	public static ItemDecoratorHandler of(ItemStack stack) {
		return DECORATOR_LOOKUP.getOrDefault(stack.getItem(), EMPTY);
	}

	public void render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		resetRenderState();
		for (IItemDecorator itemDecorator : itemDecorators) {
			if (itemDecorator.render(guiGraphics, font, stack, xOffset, yOffset))
				resetRenderState();
		}
	}

	private void resetRenderState() {
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}
}
