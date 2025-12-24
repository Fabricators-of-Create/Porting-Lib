package io.github.fabricators_of_create.porting_lib.client_events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ComputeFovModifierEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.EntityRenderersEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.MovementInputUpdateEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderArmEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderHandEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.SelectMusicEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.TextureAtlasStitchedEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

import net.minecraft.world.level.material.FogType;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ClientEventHooks {
	@Nullable
	public static Music selectMusic(Music situational, @Nullable SoundInstance playing) {
		SelectMusicEvent e = new SelectMusicEvent(situational, playing);
		return e.sendEvent().getMusic();
	}

	public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, float partialTick, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
		return new RenderHandEvent(hand, poseStack, submitNodeCollector, packedLight, partialTick, interpPitch, swingProgress, equipProgress, stack).post();
	}

	public static boolean renderSpecificFirstPersonArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AbstractClientPlayer player, HumanoidArm arm) {
		return new RenderArmEvent(poseStack, submitNodeCollector, packedLight, player, arm).post();
	}

	public static void onSetupFog(@Nullable FogEnvironment environment, FogType type, Camera camera, float partialTick, float renderDistance, FogData fogData) {
		// Modify fog rendering depending on the fluid
//		FluidState state = camera.entity().level().getFluidState(camera.blockPosition());
//		if (camera.position().y < (double) ((float) camera.blockPosition().getY() + state.getHeight(camera.entity().level(), camera.blockPosition())))
//			IClientFluidTypeExtensions.of(state).modifyFogRender(camera, environment, renderDistance, partialTick, fogData);

		new ViewportEvent.RenderFog(environment, type, camera, partialTick, fogData).sendEvent();
	}

	public static Vector4f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
		// Modify fog color depending on the fluid
//		FluidState state = level.getFluidState(camera.blockPosition());
//		if (camera.position().y < (double) ((float) camera.blockPosition().getY() + state.getHeight(level, camera.blockPosition())))
//			fluidFogColor = IClientFluidTypeExtensions.of(state).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);

		ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(camera, partialTick, fluidFogColor);
		event.sendEvent();

		fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
		return fluidFogColor;
	}

	public static Vector4f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue) {
		return getFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, new Vector4f(fogRed, fogGreen, fogBlue, 1F));
	}

	public static float getFieldOfViewModifier(Player entity, float fovModifier, float fovScale) {
		return getFieldOfViewModifier(entity, fovModifier, fovScale, 1.0F, args -> Mth.lerp((float) args[0], (float) args[1], (float) args[2]));
	}

	public static float getFieldOfViewModifier(Player entity, float fovModifier, float fovScale, float start, Operation<Float> operation) {
		ComputeFovModifierEvent fovModifierEvent = new ComputeFovModifierEvent(entity, fovModifier, fovScale, start, operation);
		return fovModifierEvent.sendEvent().getNewFovModifier();
	}

	public static float getFieldOfView(GameRenderer renderer, Camera camera, float partialTick, float fov, boolean usedConfiguredFov) {
		ViewportEvent.ComputeFov event = new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov);
		return event.sendEvent().getFOV();
	}

	public static boolean loadEntityShader(@Nullable Entity entity, GameRenderer gameRenderer) {
		if (entity != null) {
			Identifier shader = EntitySpectatorShaderManager.get(entity.getType());
			if (shader != null) {
				gameRenderer.setPostEffect(shader);
				return true;
			}
		}
		return false;
	}

	public static void onMovementInputUpdate(Player player, ClientInput movementInput) {
		new MovementInputUpdateEvent(player, movementInput).sendEvent();
	}

	public static boolean onMouseButtonPre(MouseButtonInfo mouseButtonInfo, int action) {
		return new InputEvent.MouseButton.Pre(mouseButtonInfo, action).post();
	}

	public static void onMouseButtonPost(MouseButtonInfo mouseButtonInfo, int action) {
		new InputEvent.MouseButton.Post(mouseButtonInfo, action).sendEvent();
	}

	public static boolean onMouseScroll(MouseHandler mouseHelper, double scrollDeltaX, double scrollDeltaY) {
		var event = new InputEvent.MouseScrollingEvent(scrollDeltaX, scrollDeltaY, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos());
		return event.post();
	}

	public static void onKeyInput(KeyEvent keyEvent, int action) {
		new InputEvent.Key(keyEvent, action).sendEvent();
	}

	public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand) {
		InputEvent.InteractionKeyMappingTriggered event = new InputEvent.InteractionKeyMappingTriggered(button, keyBinding, hand);
		return event.sendEvent();
	}

	public static void onTextureAtlasStitched(TextureAtlas atlas) {
		new TextureAtlasStitchedEvent(atlas).sendEvent();
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
