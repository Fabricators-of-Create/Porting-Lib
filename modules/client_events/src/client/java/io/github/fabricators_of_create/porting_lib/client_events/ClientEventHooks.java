package io.github.fabricators_of_create.porting_lib.client_events;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.EntityRenderersEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderArmEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderHandEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientEventHooks {
	public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
		return new RenderHandEvent(hand, poseStack, bufferSource, packedLight, partialTick, interpPitch, swingProgress, equipProgress, stack).post();
	}

	public static boolean renderSpecificFirstPersonArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm) {
		return new RenderArmEvent(poseStack, multiBufferSource, packedLight, player, arm).post();
	}

	public static Vector4f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
		// Modify fog color depending on the fluid
		FluidState state = level.getFluidState(camera.blockPosition());
		if (camera.position().y < (double) ((float) camera.blockPosition().getY() + state.getHeight(level, camera.blockPosition())))
			fluidFogColor = IClientFluidTypeExtensions.of(state).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);

		ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(camera, partialTick, fluidFogColor);
		event.sendEvent();

		fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
		return fluidFogColor;
	}

	public static Vector4f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue) {
		return getFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, new Vector4f(fogRed, fogGreen, fogBlue, 1F));
	}

	private static final Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> skullModelsByType = new HashMap<>();

	@Nullable
	public static SkullModelBase getModdedSkullModel(EntityModelSet modelSet, SkullBlock.Type type) {
		return skullModelsByType.getOrDefault(type, set -> null).apply(modelSet);
	}

	@ApiStatus.Internal
	public static void reloadSkullModels() {
		skullModelsByType.clear();
		new EntityRenderersEvent.CreateSkullModels(skullModelsByType, SkullBlockRenderer.SKIN_BY_TYPE).sendEvent();
	}
}
