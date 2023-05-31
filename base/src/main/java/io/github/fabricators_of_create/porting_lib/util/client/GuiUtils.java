package io.github.fabricators_of_create.porting_lib.util.client;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Use {@link ScreenUtils} instead.
 */
@Deprecated(forRemoval = true)
public class GuiUtils { // name is this to maintain max compat with upstream
	public static final int DEFAULT_BACKGROUND_COLOR = ScreenUtils.DEFAULT_BACKGROUND_COLOR;
	public static final int DEFAULT_BORDER_COLOR_START = ScreenUtils.DEFAULT_BORDER_COLOR_START;
	public static final int DEFAULT_BORDER_COLOR_END = ScreenUtils.DEFAULT_BORDER_COLOR_END;
	public static void drawGradientRect(Matrix4f matrix, int z, int left, int top, int right, int bottom, int startColor, int endColor) {
		ScreenUtils.drawGradientRect(matrix, z, left, top, right, bottom, startColor, endColor);
	}

	public static void drawHoveringText(PoseStack mStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
		ScreenUtils.drawHoveringText(mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
	}

	public static void drawHoveringText(@Nonnull final ItemStack stack, PoseStack mStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
		ScreenUtils.drawHoveringText(stack, mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
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
		ScreenUtils.drawContinuousTexturedBox(poseStack, res, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
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
		ScreenUtils.drawContinuousTexturedBox(poseStack, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
	}

	public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel) {
		ScreenUtils.drawTexturedModalRect(poseStack, x, y, u, v, width, height, zLevel);
	}
}
