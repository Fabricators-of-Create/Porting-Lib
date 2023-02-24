package io.github.fabricators_of_create.porting_lib.util.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4f;

public class GuiUtils { // name is this to maintain max compat with upstream
	public static final int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
	public static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
	public static final int DEFAULT_BORDER_COLOR_END = (DEFAULT_BORDER_COLOR_START & 0xFEFEFE) >> 1 | DEFAULT_BORDER_COLOR_START & 0xFF000000;
	public static void drawGradientRect(Matrix4f matrix, int z, int left, int top, int right, int bottom, int startColor, int endColor) {
		float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
		float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
		float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
		float startBlue  = (float)(startColor       & 255) / 255.0F;
		float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
		float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
		float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
		float endBlue    = (float)(endColor         & 255) / 255.0F;

		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buffer.vertex(matrix, right,    top, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.vertex(matrix,  left,    top, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.vertex(matrix,  left, bottom, z).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
		buffer.vertex(matrix, right, bottom, z).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
		tessellator.end();

		RenderSystem.disableBlend();
	}

	public static void drawHoveringText(PoseStack mStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
		drawHoveringText(ItemStack.EMPTY, mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
	}

	public static void drawHoveringText(@Nonnull final ItemStack stack, PoseStack mStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
		if (!textLines.isEmpty()) {
			//RenderSystem.disableRescaleNormal();
			RenderSystem.disableDepthTest();
			int tooltipTextWidth = 0;

			for (FormattedText textLine : textLines)
			{
				int textLineWidth = font.width(textLine);
				if (textLineWidth > tooltipTextWidth)
					tooltipTextWidth = textLineWidth;
			}

			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth)
			{
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

			if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
			{
				tooltipTextWidth = maxTextWidth;
				needsWrap = true;
			}

			if (needsWrap)
			{
				int wrappedTooltipWidth = 0;
				List<FormattedText> wrappedTextLines = new ArrayList<>();
				for (int i = 0; i < textLines.size(); i++)
				{
					FormattedText textLine = textLines.get(i);
					List<FormattedText> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
					if (i == 0)
						titleLinesCount = wrappedLine.size();

					for (FormattedText line : wrappedLine)
					{
						int lineWidth = font.width(line);
						if (lineWidth > wrappedTooltipWidth)
							wrappedTooltipWidth = lineWidth;
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textLines = wrappedTextLines;

				if (mouseX > screenWidth / 2)
					tooltipX = mouseX - 16 - tooltipTextWidth;
				else
					tooltipX = mouseX + 12;
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textLines.size() > 1)
			{
				tooltipHeight += (textLines.size() - 1) * 10;
				if (textLines.size() > titleLinesCount)
					tooltipHeight += 2; // gap between title lines and next lines
			}

			if (tooltipY < 4)
				tooltipY = 4;
			else if (tooltipY + tooltipHeight + 4 > screenHeight)
				tooltipY = screenHeight - tooltipHeight - 4;

			final int zLevel = 400;

			mStack.pushPose();
			Matrix4f mat = mStack.last().pose();
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
			drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

			MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			mStack.translate(0.0D, 0.0D, zLevel);

			int tooltipTop = tooltipY;

			for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
			{
				FormattedText line = textLines.get(lineNumber);
				if (line != null)
					font.drawInBatch(Language.getInstance().getVisualOrder(line), (float)tooltipX, (float)tooltipY, -1, true, mat, renderType, Font.DisplayMode.NORMAL, 0, 15728880);

				if (lineNumber + 1 == titleLinesCount)
					tooltipY += 2;

				tooltipY += 10;
			}

			renderType.endBatch();
			mStack.popPose();

			RenderSystem.enableDepthTest();
			//RenderSystem.enableRescaleNormal();
		}
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. The provided ResourceLocation object will be bound using
	 * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
	 *
	 * @param poseStack the gui pose stack
	 * @param res the ResourceLocation object that contains the desired image
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound resource location image x offset
	 * @param v bound resource location image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the resource location image
	 * @param textureHeight the height of the box texture in the resource location image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	public static void drawContinuousTexturedBox(PoseStack poseStack, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
												 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, res);
		drawContinuousTexturedBox(poseStack, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. It is assumed that the desired texture ResourceLocation object has been bound using
	 * Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation).
	 *
	 * @param poseStack the gui pose stack
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound resource location image x offset
	 * @param v bound resource location image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the resource location image
	 * @param textureHeight the height of the box texture in the resource location image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	public static void drawContinuousTexturedBox(PoseStack poseStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
												 int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		// Draw Border
		// Top Left
		drawTexturedModalRect(poseStack, x, y, u, v, leftBorder, topBorder, zLevel);
		// Top Right
		drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
		// Bottom Left
		drawTexturedModalRect(poseStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
		// Bottom Right
		drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
			// Top Border
			drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);
			// Bottom Border
			drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

			// Throw in some filler for good measure
			for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
				drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
		}

		// Side Borders
		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
			// Left Border
			drawTexturedModalRect(poseStack, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
			// Right Border
			drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
		}
	}

	public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel) {
		final float uScale = 1f / 0x100;
		final float vScale = 1f / 0x100;

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder wr = tessellator.getBuilder();
		wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		Matrix4f matrix = poseStack.last().pose();
		wr.vertex(matrix, x        , y + height, zLevel).uv( u          * uScale, ((v + height) * vScale)).endVertex();
		wr.vertex(matrix, x + width, y + height, zLevel).uv((u + width) * uScale, ((v + height) * vScale)).endVertex();
		wr.vertex(matrix, x + width, y         , zLevel).uv((u + width) * uScale, ( v           * vScale)).endVertex();
		wr.vertex(matrix, x        , y         , zLevel).uv( u          * uScale, ( v           * vScale)).endVertex();
		tessellator.end();
	}
}
