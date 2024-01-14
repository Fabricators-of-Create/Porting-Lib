package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.client.MobEffectRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

@Mixin(Gui.class)
public class GuiMixin {
	@ModifyArg(method = "renderEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
	private <E extends MobEffectInstance> Iterable<E> shouldRenderEffect(Iterable<E> elements) {
		Collection<E> effectInstances = (Collection<E>) elements;

		return effectInstances.stream().filter(mobEffectInstance -> {
			MobEffectRenderer renderer = mobEffectInstance.getEffect().getRenderer();
			if (renderer != null)
				return renderer.isVisibleInGui(mobEffectInstance);
			return true;
		}).collect(Collectors.toList());
	}

	@WrapWithCondition(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private boolean shouldRenderGuiIcon(List<Runnable> list, Object obj, @Local(index = 0) GuiGraphics graphics, @Local(index = 8) MobEffectInstance mobEffect, @Local(index = 10) int x, @Local(index = 11) int y, @Local(index = 12) float alpha) {
		MobEffectRenderer renderer = mobEffect.getEffect().getRenderer();
		if (renderer != null)
			return !renderer.renderGuiIcon(mobEffect, (Gui) (Object) this, graphics, x, y, 0, alpha);
		return true;
	}
}
