package io.github.fabricators_of_create.porting_lib.gui.extensions;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface GuiGraphicsExtension {
	default void renderComponentTooltip(Font font, List<? extends FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
		throw new RuntimeException("Mixin failed!");
	}
}
