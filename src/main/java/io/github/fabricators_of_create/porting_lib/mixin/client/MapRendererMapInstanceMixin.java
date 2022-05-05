package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import io.github.fabricators_of_create.porting_lib.extensions.MapDecorationExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMapInstanceMixin {
	@Shadow
	private boolean requiresUpload;

	@Shadow
	protected abstract void updateTexture();

	@Shadow
	@Final
	private RenderType renderType;

	@Shadow
	private MapItemSavedData data;

	/**
	 * @author AlphaMode
	 * @reason mixin doesn't allow continuing in loops :why:
	 */
	@Overwrite
	void draw(PoseStack p_93292_, MultiBufferSource p_93293_, boolean p_93294_, int p_93295_) {
		if (this.requiresUpload) {
			this.updateTexture();
			this.requiresUpload = false;
		}

		int i = 0;
		int j = 0;
		float f = 0.0F;
		Matrix4f matrix4f = p_93292_.last().pose();
		VertexConsumer vertexconsumer = p_93293_.getBuffer(this.renderType);
		vertexconsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_93295_).endVertex();
		vertexconsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_93295_).endVertex();
		vertexconsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_93295_).endVertex();
		vertexconsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_93295_).endVertex();
		int k = 0;

		for(MapDecoration mapdecoration : this.data.getDecorations()) {
			if (!p_93294_ || mapdecoration.renderOnFrame()) {
				if (((MapDecorationExtensions)mapdecoration).render(k)) { k++; continue; }
				p_93292_.pushPose();
				p_93292_.translate(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F, (double)-0.02F);
				p_93292_.mulPose(Vector3f.ZP.rotationDegrees((float)(mapdecoration.getRot() * 360) / 16.0F));
				p_93292_.scale(4.0F, 4.0F, 3.0F);
				p_93292_.translate(-0.125D, 0.125D, 0.0D);
				byte b0 = mapdecoration.getImage();
				float f1 = (float)(b0 % 16 + 0) / 16.0F;
				float f2 = (float)(b0 / 16 + 0) / 16.0F;
				float f3 = (float)(b0 % 16 + 1) / 16.0F;
				float f4 = (float)(b0 / 16 + 1) / 16.0F;
				Matrix4f matrix4f1 = p_93292_.last().pose();
				float f5 = -0.001F;
				VertexConsumer vertexconsumer1 = p_93293_.getBuffer(MapRenderer.MAP_ICONS);
				vertexconsumer1.vertex(matrix4f1, -1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f2).uv2(p_93295_).endVertex();
				vertexconsumer1.vertex(matrix4f1, 1.0F, 1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f2).uv2(p_93295_).endVertex();
				vertexconsumer1.vertex(matrix4f1, 1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f3, f4).uv2(p_93295_).endVertex();
				vertexconsumer1.vertex(matrix4f1, -1.0F, -1.0F, (float)k * -0.001F).color(255, 255, 255, 255).uv(f1, f4).uv2(p_93295_).endVertex();
				p_93292_.popPose();
				if (mapdecoration.getName() != null) {
					Font font = Minecraft.getInstance().font;
					Component component = mapdecoration.getName();
					float f6 = (float)font.width(component);
					float f7 = Mth.clamp(25.0F / f6, 0.0F, 6.0F / 9.0F);
					p_93292_.pushPose();
					p_93292_.translate(0.0F + (float)mapdecoration.getX() / 2.0F + 64.0F - f6 * f7 / 2.0F, 0.0F + (float)mapdecoration.getY() / 2.0F + 64.0F + 4.0F, -0.025F);
					p_93292_.scale(f7, f7, 1.0F);
					p_93292_.translate(0.0D, 0.0D, -0.1F);
					font.drawInBatch(component, 0.0F, 0.0F, -1, false, p_93292_.last().pose(), p_93293_, false, Integer.MIN_VALUE, p_93295_);
					p_93292_.popPose();
				}

				++k;
			}
		}

	}
}
