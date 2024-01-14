package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;

public class RenderPlayerEvents {
	/**
	 * Fired <b>before</b> the player is rendered.
	 * This can be used for rendering additional effects or suppressing rendering.
	 * {@link RenderPlayerEvents#POST} will not be fired.</p>
	 * <p>
	 * If this event is cancelled, then the player will not be rendered and the corresponding
	 */
	public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (player, renderer, partialTick, poseStack, buffer, packedLight) -> {
		for (Pre callback : callbacks)
			if (callback.onPreRenderPlayer(player, renderer, partialTick, poseStack, buffer, packedLight))
				return true;
		return false;
	});

	/**
	 * Fired <b>after</b> the player is rendered, if the corresponding {@link RenderPlayerEvents#PRE} is not cancelled.
	 */
	public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (player, renderer, partialTick, poseStack, buffer, packedLight) -> {
		for (Post callback : callbacks)
			callback.onPostRenderPlayer(player, renderer, partialTick, poseStack, buffer, packedLight);
	});


	public interface Pre {
		/**
		 *
		 * @param player The player being rendered
		 * @param renderer The current instance that is rendering the player.
		 * @param partialTick
		 * @param poseStack
		 * @param buffer
		 * @param packedLight
		 * @return return true to cancel rendering
		 */
		boolean onPreRenderPlayer(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight);
	}

	public interface Post {
		void onPostRenderPlayer(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight);
	}
}
