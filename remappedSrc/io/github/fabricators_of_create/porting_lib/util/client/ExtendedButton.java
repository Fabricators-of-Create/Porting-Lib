package io.github.fabricators_of_create.porting_lib.util.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * This class provides a button that fixes several bugs present in the vanilla GuiButton drawing code.
 * The gist of it is that it allows buttons of any size without gaps in the graphics and with the
 * borders drawn properly. It also prevents button text from extending out of the sides of the button by
 * trimming the end of the string and adding an ellipsis.<br/><br/>
 *
 * The code that handles drawing the button is in GuiUtils.
 *
 * @author bspkrs
 */
public class ExtendedButton extends ButtonWidget {
	public ExtendedButton(int xPos, int yPos, int width, int height, Text displayString, PressAction handler) {
		super(xPos, yPos, width, height, displayString, handler);
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
		MinecraftClient mc = MinecraftClient.getInstance();
		int k = this.getYImage(this.hovered);
		GuiUtils.drawContinuousTexturedBox(poseStack, WIDGETS_TEXTURE, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getZOffset());
		this.renderBackground(poseStack, mc, mouseX, mouseY);

		Text buttonText = this.getMessage();
		int strWidth = mc.textRenderer.getWidth(buttonText);
		int ellipsisWidth = mc.textRenderer.getWidth("...");

		if (strWidth > width - 6 && strWidth > ellipsisWidth)
			//TODO, srg names make it hard to figure out how to append to an ITextProperties from this trim operation, wraping this in StringTextComponent is kinda dirty.
			buttonText = new LiteralText(mc.textRenderer.trimToWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");

		drawCenteredText(poseStack, mc.textRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, this.active ? 16777215 : 10526880);
	}
}
