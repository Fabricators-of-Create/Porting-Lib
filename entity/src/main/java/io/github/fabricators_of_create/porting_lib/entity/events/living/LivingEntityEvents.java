package io.github.fabricators_of_create.porting_lib.entity.events.living;

import javax.annotation.Nonnull;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LivingEntityEvents {
	public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
		for (Tick callback : callbacks) {
			callback.onLivingEntityTick(entity);
		}
	});

	public static final Event<Jump> JUMP = EventFactory.createArrayBacked(Jump.class, callbacks -> (entity) -> {
		for (Jump callback : callbacks) {
			callback.onLivingEntityJump(entity);
		}
	});

	public static final Event<EquipmentChange> EQUIPMENT_CHANGE = EventFactory.createArrayBacked(EquipmentChange.class, callbacks -> ((entity, slot, from, to) -> {
		for (EquipmentChange callback : callbacks)
			callback.onEquipmentChange(entity, slot, from, to);
	}));

	public static final Event<Visibility> VISIBILITY = EventFactory.createArrayBacked(Visibility.class, callbacks -> (entity, lookingEntity, originalMultiplier) -> {
		for (Visibility e : callbacks) {
			double newMultiplier = e.getEntityVisibilityMultiplier(entity, lookingEntity, originalMultiplier);
			if (newMultiplier != originalMultiplier)
				return newMultiplier;
		}
		return originalMultiplier;
	});

	@FunctionalInterface
	public interface EquipmentChange {
		void onEquipmentChange(LivingEntity entity, EquipmentSlot slot, @Nonnull ItemStack from, @Nonnull ItemStack to);
	}

	@FunctionalInterface
	public interface Tick {
		void onLivingEntityTick(LivingEntity entity);
	}

	@FunctionalInterface
	public interface Jump {
		void onLivingEntityJump(LivingEntity entity);
	}

	@FunctionalInterface
	public interface Visibility {
		double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier);
	}
}
