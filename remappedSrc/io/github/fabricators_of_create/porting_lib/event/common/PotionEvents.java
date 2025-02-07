package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class PotionEvents {
	public static Event<PotionAdded> POTION_ADDED = EventFactory.createArrayBacked(PotionAdded.class, callbacks -> (entity, newEffect, oldEffect, source) -> {
		for (PotionAdded e : callbacks)
			e.onPotionAdded(entity, newEffect, oldEffect, source);
	});

	public static Event<PotionApplicable> POTION_APPLICABLE = EventFactory.createArrayBacked(PotionApplicable.class, callbacks -> (entity, effect) -> {
		for (PotionApplicable e : callbacks) {
			ActionResult result = e.onPotionApplicable(entity, effect);
			if (result != ActionResult.PASS)
				return result;
		}
		return ActionResult.PASS;
	});

	@FunctionalInterface
	public interface PotionAdded {
		void onPotionAdded(LivingEntity entity, StatusEffectInstance newEffect, StatusEffectInstance oldEffect, @Nullable Entity source);
	}

	@FunctionalInterface
	public interface PotionApplicable {
		ActionResult onPotionApplicable(LivingEntity entity, StatusEffectInstance effect);
	}
}
