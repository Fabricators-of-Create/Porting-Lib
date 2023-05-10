package io.github.fabricators_of_create.porting_lib.entity.testmod;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.entity.testmod.CustomSlime.OrbitingItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class CustomSlimeRenderer extends SlimeRenderer {
	private final ItemRenderer itemRenderer;

	public CustomSlimeRenderer(Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(@NotNull Slime slime, float yaw, float tickDelta, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light) {
		super.render(slime, yaw, tickDelta, poseStack, buffers, light);
		if (!(slime instanceof CustomSlime custom))
			return;
		OrbitingItem item = custom.item;
		BakedModel model = itemRenderer.getModel(item.stack, slime.level, slime, 0);
		poseStack.pushPose();
		Vec3 offset = item.getOffset(tickDelta);
		poseStack.translate(offset.x, offset.y, offset.z);
		itemRenderer.render(
				item.stack, ItemDisplayContext.FIXED, false, poseStack, buffers, light, OverlayTexture.NO_OVERLAY, model
		);
		poseStack.popPose();
	}
}
