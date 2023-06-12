package io.github.fabricators_of_create.porting_lib.entity.events;

import javax.annotation.Nonnull;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LivingEntityUseItemEvents {

	Event<LivingUseItemFinish> LIVING_USE_ITEM_FINISH = EventFactory.createArrayBacked(LivingUseItemFinish.class, callbacks -> (entity, item, duration, result) -> {
		for(LivingUseItemFinish e : callbacks) {
			ItemStack itemStack = e.onUseItem(entity, item, duration, result);
			if(itemStack != null)
				return itemStack;
		}
		return null;
	});

	@FunctionalInterface
	interface LivingUseItemFinish {
		ItemStack onUseItem(LivingEntity entity, @Nonnull ItemStack item, int duration, @Nonnull ItemStack result);
	}
}
