package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import java.util.Collection;
import java.util.stream.Collectors;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.fabricators_of_create.porting_lib.entity.client.MobEffectRenderer;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {
	@ModifyArg(method = "renderEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private <E extends MobEffectInstance> Iterable<E> shouldRenderEffect(Iterable<E> elements) {
		Collection<E> effectInstances = (Collection<E>) elements;

		return effectInstances.stream().filter(mobEffectInstance -> {
			MobEffectRenderer renderer = mobEffectInstance.getEffect().getRenderer();
			if (renderer != null)
				return renderer.isVisibleInInventory(mobEffectInstance);
			return true;
		}).collect(Collectors.toList());
	}

	@WrapOperation(method = "renderIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
	private void test(GuiGraphics graphics, int blitX, int blitY, int z, int width, int height, TextureAtlasSprite sprite, Operation<Void> original, @Local(index = 2) int x, @Local(index = 3) int offsetDelta, @Local(index = 5) boolean wide, @Local(index = 9) MobEffectInstance mobEffectInstance, @Local(index = 7) int i, @Share("offset") LocalRef<Integer> offset) {
		if (offset.get() == null)
			offset.set(0);
		MobEffectRenderer renderer = mobEffectInstance.getEffect().getRenderer();
		if (renderer != null && renderer.renderInventoryIcon(mobEffectInstance, (EffectRenderingInventoryScreen) (Object) this, graphics, x + (wide ? 6 : 7), offset.get() + i, 0)) {
			offset.set(offset.get() + offsetDelta);
			return;
		}
		original.call(graphics, blitX, blitY + offset.get(), z, width, height, sprite);
	}

	@Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;getEffectName(Lnet/minecraft/world/effect/MobEffectInstance;)Lnet/minecraft/network/chat/Component;"))
	private void renderCustomInventoryText(GuiGraphics graphics, int x, int height, Iterable<MobEffectInstance> statusEffects, CallbackInfo ci, @Local(index = 7) MobEffectInstance mobEffectInstance, @Local(index = 5) int i, @Share("custom") LocalRef<Boolean> cancelled) {
		MobEffectRenderer renderer = mobEffectInstance.getEffect().getRenderer();
		if (renderer != null && renderer.renderInventoryText(mobEffectInstance, (EffectRenderingInventoryScreen<?>) (Object) this, graphics, x, i, 0))
			cancelled.set(true);
		else
			cancelled.set(false);
	}

	@WrapWithCondition(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
	private boolean cancelInventoryText(GuiGraphics graphics, Font renderer, Component text, int x, int y, int color, @Share("custom") LocalRef<Boolean> cancelled) {
		return !cancelled.get();
	}
}
