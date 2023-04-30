package io.github.fabricators_of_create.porting_lib.entity.events.living;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LivingEntityFinishUsingItemCallback {
	/**
	 * Fired when a living entity finishes using an item. Examples:
	 * <ul>
	 *     <li>Drawing a crossbow</li>
	 *     <li>Eating food</li>
	 *     <li>Drinking a potion</li>
	 * </ul>
	 */
	Event<LivingEntityFinishUsingItemCallback> EVENT = EventFactory.createArrayBacked(LivingEntityFinishUsingItemCallback.class, callbacks -> (entity, original, used) -> {
		for(LivingEntityFinishUsingItemCallback callback : callbacks) {
			ItemStack result = callback.modifyUseResult(entity, original, used);
			if (result != null)
				return result;
		}
		return null;
	});

	/**
	 * @return a modified use result, or null if unchanged
	 */
	@Nullable
	ItemStack modifyUseResult(LivingEntity entity, ItemStack original, ItemStack used);
}
