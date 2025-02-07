package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

/**
 * Fired before the player's arm is rendered in first person. This is a more targeted version of {@link RenderHandCallback.RenderHandEvent},
 * and can be used to replace the rendering of the player's arm, such as for rendering armor on the arm or outright
 * replacing the arm with armor.
 *
 * <p>If this event is cancelled, then the arm will not be rendered.</p>
 * <p>Called before {@link net.minecraft.client.render.entity.PlayerEntityRenderer#renderArm(MatrixStack, VertexConsumerProvider, int, AbstractClientPlayerEntity, ModelPart, ModelPart)}</p>
 */
public interface RenderArmCallback {
	Event<RenderArmCallback> EVENT = EventFactory.createArrayBacked(RenderArmCallback.class, callbacks -> (poseStack, buffer, packedLight, player, arm) -> {
		for (RenderArmCallback e : callbacks)
			if (e.onRenderArm(poseStack, buffer, packedLight, player, arm))
				return true;
		return false;
	});

	/**
	 *
	 * @param poseStack
	 * @param buffer
	 * @param packedLight
	 * @param player The player
	 * @param arm The current arm that will be rendered.
	 * @return true to cancel further processing
	 */
	boolean onRenderArm(MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, AbstractClientPlayerEntity player, Arm arm);
}
