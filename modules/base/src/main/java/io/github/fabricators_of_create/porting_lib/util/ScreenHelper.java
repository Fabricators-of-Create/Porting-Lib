package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.event.client.RenderTooltipBorderColorCallback;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor.ScreenAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ScreenHelper {
	public static final int DEFAULT_BORDER_COLOR_START = 1347420415;
	public static final int DEFAULT_BORDER_COLOR_END = 1344798847;
	public static RenderTooltipBorderColorCallback.BorderColorEntry CURRENT_COLOR;

	private static final Deque<ItemStack> tooltipStacks = new ArrayDeque<>();

	public static Minecraft getClient(Screen screen) {
		return ((ScreenAccessor) screen).port_lib$getMinecraft();
	}

	public static void withTooltipStack(ItemStack stack, Runnable runnable) {
		try {
			tooltipStacks.push(stack);
			runnable.run();
		} finally {
			tooltipStacks.pop();
		}
	}

	public static ItemStack currentTooltipStack() {
		ItemStack stack = tooltipStacks.peek();
		return stack != null ? stack : ItemStack.EMPTY;
	}
}
