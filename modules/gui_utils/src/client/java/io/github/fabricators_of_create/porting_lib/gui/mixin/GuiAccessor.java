package io.github.fabricators_of_create.porting_lib.gui.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.components.SubtitleOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {
	@Accessor
	SubtitleOverlay getSubtitleOverlay();

	@Invoker
	void callRenderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderScoreboardSidebar(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

	@Invoker
	void callRenderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
