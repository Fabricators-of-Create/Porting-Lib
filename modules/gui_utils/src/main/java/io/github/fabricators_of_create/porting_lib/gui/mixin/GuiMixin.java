package io.github.fabricators_of_create.porting_lib.gui.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.gui.layered.GuiLayerManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

import net.minecraft.client.gui.components.SubtitleOverlay;

import net.minecraft.client.gui.components.spectator.SpectatorGui;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.PlayerRideableJumping;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.fabricators_of_create.porting_lib.gui.layered.VanillaGuiLayers.*;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	protected abstract void renderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderScoreboardSidebar(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	public abstract void renderSavingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	protected abstract void renderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Shadow
	@Final
	private Minecraft minecraft;

	@Accessor
	public abstract SubtitleOverlay getSubtitleOverlay();

	@Unique
	private final GuiLayerManager port_lib$layerManager = new GuiLayerManager();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initCustomLayers(Minecraft minecraft, CallbackInfo ci, @Local(ordinal = 0) LayeredDraw layeredDraw) {
		GuiLayerManager playerHealthComponents = new GuiLayerManager()
				.addVanilla(PLAYER_HEALTH)
				.addVanilla(ARMOR_LEVEL)
				.addVanilla(FOOD_LEVEL);

		// Otherwise, GuiMixin is used as a param in the lambdas instead of Gui when used as a local.
		Gui gui = (Gui) (Object) this;

		GuiLayerManager mainLayers = new GuiLayerManager()
				.addVanilla(CAMERA_OVERLAYS, this::renderCameraOverlays)
				.addVanilla(CROSSHAIR, this::renderCrosshair)
				.addVanilla(HOTBAR)
				.addVanilla(JUMP_METER)
				.addVanilla(EXPERIENCE_BAR)
				.add(playerHealthComponents, () -> minecraft.gameMode.canHurtPlayer())
				.addVanilla(VEHICLE_HEALTH)
				.addVanilla(AIR_LEVEL)
				.addVanilla(SELECTED_ITEM_NAME)
				.addVanilla(SPECTATOR_TOOLTIP)
				.addVanilla(EXPERIENCE_LEVEL, this::renderExperienceLevel)
				.addVanilla(EFFECTS, this::renderEffects)
				.addVanilla(BOSS_OVERLAY, (guiGraphics, tickDelta) -> gui.getBossOverlay().render(guiGraphics));

		GuiLayerManager additionalLayers = new GuiLayerManager()
				.addVanilla(DEMO_OVERLAY, this::renderDemoOverlay)
				.addVanilla(DEBUG_OVERLAY, (guiGraphics, deltaTracker) -> {
					if (gui.getDebugOverlay().showDebugScreen()) {
						gui.getDebugOverlay().render(guiGraphics);
					}
				})
				.addVanilla(SCOREBOARD_SIDEBAR, this::renderScoreboardSidebar)
				.addVanilla(OVERLAY_MESSAGE, this::renderOverlayMessage)
				.addVanilla(TITLE, this::renderTitle)
				.addVanilla(CHAT, this::renderChat)
				.addVanilla(TAB_LIST, this::renderTabList)
				.addVanilla(SUBTITLE_OVERLAY, (guiGraphics, deltaTracker) -> ((GuiMixin) (Object) gui).getSubtitleOverlay().render(guiGraphics))
				.addVanilla(SAVING_INDICATOR, this::renderSavingIndicator);

		port_lib$layerManager
				.add(mainLayers, () -> !minecraft.options.hideGui)
				.addVanilla(SLEEP_OVERLAY, this::renderSleepOverlay)
				.add(additionalLayers, () -> !minecraft.options.hideGui);

		port_lib$layerManager.initModdedLayers();
	}

	@Unique
	private void port_lib$tryRenderLayer(ResourceLocation layerId, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Runnable renderCallback) {
		if (!port_lib$layerManager.callPreRenderEvent(layerId, guiGraphics, deltaTracker)) {
			renderCallback.run();
			port_lib$layerManager.callPostRenderEvent(layerId, guiGraphics, deltaTracker);
		}

		port_lib$layerManager.renderFrom(layerId, guiGraphics, deltaTracker);
	}

	@WrapMethod(method = "method_55808")
	private void tryRenderBossOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(BOSS_OVERLAY, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;renderHotbar(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderHotbar(SpectatorGui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		port_lib$tryRenderLayer(HOTBAR, guiGraphics, deltaTracker, () -> original.call(instance, guiGraphics));
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void tryRenderHotbar(Gui instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(HOTBAR, guiGraphics, deltaTracker, () -> original.call(instance, guiGraphics, deltaTracker));
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
	private void renderJumpAndExperienceLayers(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		// TODO: this renders before jump meter and experience bar, but we can't wrap blocks of code.
		port_lib$layerManager.renderFrom(JUMP_METER, guiGraphics, deltaTracker);
		port_lib$layerManager.renderFrom(EXPERIENCE_BAR, guiGraphics, deltaTracker);
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lnet/minecraft/world/entity/PlayerRideableJumping;Lnet/minecraft/client/gui/GuiGraphics;I)V"))
	private void tryRenderJumpBar(Gui instance, PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(JUMP_METER, guiGraphics, deltaTracker)) {
			original.call(instance, rideable, guiGraphics, x);
			port_lib$layerManager.callPostRenderEvent(JUMP_METER, guiGraphics, deltaTracker);
		}
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V"))
	private void tryRenderExperienceBar(Gui instance, GuiGraphics guiGraphics, int x, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(EXPERIENCE_BAR, guiGraphics, deltaTracker)) {
			original.call(instance, guiGraphics, x);
			port_lib$layerManager.callPostRenderEvent(EXPERIENCE_BAR, guiGraphics, deltaTracker);
		}
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderPlayerBar(Gui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(EXPERIENCE_BAR, guiGraphics, deltaTracker)) {
			original.call(instance, guiGraphics);
			port_lib$layerManager.callPostRenderEvent(EXPERIENCE_BAR, guiGraphics, deltaTracker);
		}
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderPlayerHealth(Gui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(PLAYER_HEALTH, guiGraphics, deltaTracker)) {
			original.call(instance, guiGraphics);
			port_lib$layerManager.callPostRenderEvent(PLAYER_HEALTH, guiGraphics, deltaTracker);
		}
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void renderPlayerHealthLayers(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		port_lib$layerManager.renderFrom(PLAYER_HEALTH, guiGraphics, deltaTracker);
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderVehicleHealth(Gui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		port_lib$tryRenderLayer(VEHICLE_HEALTH, guiGraphics, deltaTracker, () -> original.call(instance, guiGraphics));
	}

	@Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 1))
	private void renderTooltipLayers(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		// TODO: this renders before selected item name and spectator tooltip, but we can't wrap blocks of code.
		port_lib$layerManager.renderFrom(SELECTED_ITEM_NAME, guiGraphics, deltaTracker);
		port_lib$layerManager.renderFrom(SPECTATOR_TOOLTIP, guiGraphics, deltaTracker);
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderSelectedItemName(Gui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(SELECTED_ITEM_NAME, guiGraphics, deltaTracker)) {
			original.call(instance, guiGraphics);
			port_lib$layerManager.callPostRenderEvent(SELECTED_ITEM_NAME, guiGraphics, deltaTracker);
		}
	}

	@WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;)V"))
	private void tryRenderSpectatorTooltip(SpectatorGui instance, GuiGraphics guiGraphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (!port_lib$layerManager.callPreRenderEvent(SPECTATOR_TOOLTIP, guiGraphics, deltaTracker)) {
			original.call(instance, guiGraphics);
			port_lib$layerManager.callPostRenderEvent(SPECTATOR_TOOLTIP, guiGraphics, deltaTracker);
		}
	}

	@WrapMethod(method = "renderPlayerHealth")
	private void tryRenderPlayerHealth(GuiGraphics guiGraphics, Operation<Void> original) {
		// TODO: try to split it apart
		if (!port_lib$layerManager.callPreRenderEvent(PLAYER_HEALTH, guiGraphics, minecraft.getTimer())
				&& !port_lib$layerManager.callPreRenderEvent(ARMOR_LEVEL, guiGraphics, minecraft.getTimer())
				&& !port_lib$layerManager.callPreRenderEvent(FOOD_LEVEL, guiGraphics, minecraft.getTimer())
				&& !port_lib$layerManager.callPreRenderEvent(AIR_LEVEL, guiGraphics, minecraft.getTimer())
		) {
			original.call(guiGraphics);
			port_lib$layerManager.callPostRenderEvent(PLAYER_HEALTH, guiGraphics, minecraft.getTimer());
			port_lib$layerManager.callPostRenderEvent(ARMOR_LEVEL, guiGraphics, minecraft.getTimer());
			port_lib$layerManager.callPostRenderEvent(FOOD_LEVEL, guiGraphics, minecraft.getTimer());
			port_lib$layerManager.callPostRenderEvent(AIR_LEVEL, guiGraphics, minecraft.getTimer());
		}

		port_lib$layerManager.renderFrom(PLAYER_HEALTH, guiGraphics, minecraft.getTimer());
		port_lib$layerManager.renderFrom(ARMOR_LEVEL, guiGraphics, minecraft.getTimer());
		port_lib$layerManager.renderFrom(FOOD_LEVEL, guiGraphics, minecraft.getTimer());
		port_lib$layerManager.renderFrom(AIR_LEVEL, guiGraphics, minecraft.getTimer());
	}

	// Main layers
	@WrapMethod(method = "renderCameraOverlays")
	private void tryRenderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(CAMERA_OVERLAYS, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderCrosshair")
	private void tryRenderCrosshairs(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(CROSSHAIR, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderExperienceLevel")
	private void tryRenderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(EXPERIENCE_LEVEL, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderEffects")
	private void tryRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(EFFECTS, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	// Additional layers
	@WrapMethod(method = "renderDemoOverlay")
	private void tryRenderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(DEMO_OVERLAY, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "method_55807")
	private void tryRenderDebugOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(DEBUG_OVERLAY, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderScoreboardSidebar")
	private void tryRenderSidebarOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(SCOREBOARD_SIDEBAR, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderOverlayMessage")
	private void tryRenderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(OVERLAY_MESSAGE, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderTitle")
	private void tryRenderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(TITLE, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderChat")
	private void tryRenderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(CHAT, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderTabList")
	private void tryRenderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(TAB_LIST, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "method_55806")
	private void tryRenderSubtitleOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(SUBTITLE_OVERLAY, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderSavingIndicator")
	private void tryRenderSaving(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(SAVING_INDICATOR, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}

	@WrapMethod(method = "renderSleepOverlay")
	private void tryRenderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		port_lib$tryRenderLayer(SLEEP_OVERLAY, guiGraphics, deltaTracker, () -> original.call(guiGraphics, deltaTracker));
	}
}
