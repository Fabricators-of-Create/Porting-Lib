package io.github.fabricators_of_create.porting_lib.event.common;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PotionEvents {
	public static Event<PotionAdded> POTION_ADDED = EventFactory.createArrayBacked(PotionAdded.class, callbacks -> (entity, newEffect, oldEffect, source) -> {
		for (PotionAdded e : callbacks)
			e.onPotionAdded(entity, newEffect, oldEffect, source);
	});

	public static Event<PotionApplicable> POTION_APPLICABLE = EventFactory.createArrayBacked(PotionApplicable.class, callbacks -> (entity, effect) -> {
		for (PotionApplicable e : callbacks) {
			InteractionResult result = e.onPotionApplicable(entity, effect);
			if (result != InteractionResult.PASS)
				return result;
		}
		return InteractionResult.PASS;
	});

	@FunctionalInterface
	public interface PotionAdded {
		void onPotionAdded(LivingEntity entity, MobEffectInstance newEffect, MobEffectInstance oldEffect, @Nullable Entity source);
	}

	@FunctionalInterface
	public interface PotionApplicable {
		InteractionResult onPotionApplicable(LivingEntity entity, MobEffectInstance effect);
	}
}
