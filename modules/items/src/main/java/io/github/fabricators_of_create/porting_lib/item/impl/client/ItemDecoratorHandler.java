package io.github.fabricators_of_create.porting_lib.item.impl.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.fabricators_of_create.porting_lib.item.api.client.IItemDecorator;
import io.github.fabricators_of_create.porting_lib.item.api.client.callbacks.ItemDecorationsCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@ApiStatus.Internal
public final class ItemDecoratorHandler {
	private final List<IItemDecorator> itemDecorators;
	private final GlStateBackup stateBackup = new GlStateBackup();

	private static Map<Item, ItemDecoratorHandler> DECORATOR_LOOKUP = ImmutableMap.of();

	private static final ItemDecoratorHandler EMPTY = new ItemDecoratorHandler();

	private ItemDecoratorHandler() {
		this.itemDecorators = ImmutableList.of();
	}

	private ItemDecoratorHandler(List<IItemDecorator> itemDecorators) {
		this.itemDecorators = ImmutableList.copyOf(itemDecorators);
	}

	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			var decorators = new HashMap<Item, List<IItemDecorator>>();
			ItemDecorationsCallback.EVENT.invoker().registerDecorators(decorators);
			var builder = new ImmutableMap.Builder<Item, ItemDecoratorHandler>();
			decorators.forEach((item, itemDecorators) -> builder.put(item, new ItemDecoratorHandler(itemDecorators)));
			DECORATOR_LOOKUP = builder.build();
		});
	}

	public static ItemDecoratorHandler of(ItemStack stack) {
		return DECORATOR_LOOKUP.getOrDefault(stack.getItem(), EMPTY);
	}

	public void render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		backupGlState(stateBackup);

		resetRenderState();
		for (IItemDecorator itemDecorator : itemDecorators) {
			if (itemDecorator.render(guiGraphics, font, stack, xOffset, yOffset))
				resetRenderState();
		}

		restoreGlState(stateBackup);
	}

	private void resetRenderState() {
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}

	public static void backupGlState(GlStateBackup state) {
		RenderSystem.assertOnRenderThread();
		_backupGlState(state);
	}

	public static void restoreGlState(GlStateBackup state) {
		RenderSystem.assertOnRenderThread();
		_restoreGlState(state);
	}

	public static void _backupGlState(GlStateBackup state) {
		state.blendEnabled = GlStateManager.BLEND.mode.enabled;
		state.blendSrcRgb = GlStateManager.BLEND.srcRgb;
		state.blendDestRgb = GlStateManager.BLEND.dstRgb;
		state.blendSrcAlpha = GlStateManager.BLEND.srcAlpha;
		state.blendDestAlpha = GlStateManager.BLEND.dstAlpha;
		state.depthEnabled = GlStateManager.DEPTH.mode.enabled;
		state.depthMask = GlStateManager.DEPTH.mask;
		state.depthFunc = GlStateManager.DEPTH.func;
		state.cullEnabled = GlStateManager.CULL.enable.enabled;
		state.polyOffsetFillEnabled = GlStateManager.POLY_OFFSET.fill.enabled;
		state.polyOffsetLineEnabled = GlStateManager.POLY_OFFSET.line.enabled;
		state.polyOffsetFactor = GlStateManager.POLY_OFFSET.factor;
		state.polyOffsetUnits = GlStateManager.POLY_OFFSET.units;
		state.colorLogicEnabled = GlStateManager.COLOR_LOGIC.enable.enabled;
		state.colorLogicOp = GlStateManager.COLOR_LOGIC.op;
		state.stencilFuncFunc = GlStateManager.STENCIL.func.func;
		state.stencilFuncRef = GlStateManager.STENCIL.func.ref;
		state.stencilFuncMask = GlStateManager.STENCIL.func.mask;
		state.stencilMask = GlStateManager.STENCIL.mask;
		state.stencilFail = GlStateManager.STENCIL.fail;
		state.stencilZFail = GlStateManager.STENCIL.zfail;
		state.stencilZPass = GlStateManager.STENCIL.zpass;
		state.scissorEnabled = GlStateManager.SCISSOR.mode.enabled;
		state.colorMaskRed = GlStateManager.COLOR_MASK.red;
		state.colorMaskGreen = GlStateManager.COLOR_MASK.green;
		state.colorMaskBlue = GlStateManager.COLOR_MASK.blue;
		state.colorMaskAlpha = GlStateManager.COLOR_MASK.alpha;
	}

	public static void _restoreGlState(GlStateBackup state) {
		GlStateManager.BLEND.mode.setEnabled(state.blendEnabled);
		GlStateManager._blendFuncSeparate(state.blendSrcRgb, state.blendDestRgb, state.blendSrcAlpha, state.blendDestAlpha);
		GlStateManager.DEPTH.mode.setEnabled(state.depthEnabled);
		GlStateManager._depthMask(state.depthMask);
		GlStateManager._depthFunc(state.depthFunc);
		GlStateManager.CULL.enable.setEnabled(state.cullEnabled);
		GlStateManager.POLY_OFFSET.fill.setEnabled(state.polyOffsetFillEnabled);
		GlStateManager.POLY_OFFSET.line.setEnabled(state.polyOffsetLineEnabled);
		GlStateManager._polygonOffset(state.polyOffsetFactor, state.polyOffsetUnits);
		GlStateManager.COLOR_LOGIC.enable.setEnabled(state.colorLogicEnabled);
		GlStateManager._logicOp(state.colorLogicOp);
		GlStateManager._stencilFunc(state.stencilFuncFunc, state.stencilFuncRef, state.stencilFuncMask);
		GlStateManager._stencilMask(state.stencilMask);
		GlStateManager._stencilOp(state.stencilFail, state.stencilZFail, state.stencilZPass);
		GlStateManager.SCISSOR.mode.setEnabled(state.scissorEnabled);
		GlStateManager._colorMask(state.colorMaskRed, state.colorMaskGreen, state.colorMaskBlue, state.colorMaskAlpha);
	}
}
