package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface RenderTooltipBorderColorCallback {
	int DEFAULT_BORDER_COLOR_START = 1347420415;
	int DEFAULT_BORDER_COLOR_END = 1344798847;

	Event<RenderTooltipBorderColorCallback> EVENT = EventFactory.createArrayBacked(RenderTooltipBorderColorCallback.class, callbacks -> (stack, originalBorderColorStart, originalBorderColorEnd) -> {
		for (RenderTooltipBorderColorCallback callback : callbacks) {
			BorderColorEntry entry = callback.onTooltipBorderColor(stack, originalBorderColorStart, originalBorderColorEnd);
			if (entry != null) {
				return entry;
			}
		}
		return null;
	});

	BorderColorEntry onTooltipBorderColor(ItemStack stack, int originalBorderColorStart, int originalBorderColorEnd);

	class BorderColorEntry {
		public static BorderColorEntry CURRENT_COLOR;
		private final int borderColorStart;
		private final int borderColorEnd;

		public BorderColorEntry(int start, int end) {
			borderColorStart = start;
			borderColorEnd = end;
		}

		public int getBorderColorStart() {
			return borderColorStart;
		}

		public int getBorderColorEnd() {
			return borderColorEnd;
		}
	}
}
