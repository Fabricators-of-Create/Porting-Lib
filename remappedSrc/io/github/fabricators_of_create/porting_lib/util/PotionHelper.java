package io.github.fabricators_of_create.porting_lib.util;

import java.util.Iterator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.LivingEntityAccessor;

public class PotionHelper {
	/***
	 * Removes all potion effects that have curativeItem as a curative item for its effect
	 * @param curativeItem The itemstack we are using to cure potion effects
	 */
	public static boolean curePotionEffects(LivingEntity livingEntity, ItemStack curativeItem) {
		if (livingEntity.world.isClient)
			return false;
		boolean ret = false;
		Iterator<StatusEffectInstance> itr = livingEntity.getStatusEffects().iterator();
		while (itr.hasNext()) {
			StatusEffectInstance effect = itr.next();
			if (effect.isCurativeItem(curativeItem) /*&& !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent(this, effect))*/) {
				((LivingEntityAccessor)livingEntity).port_lib$onEffectRemoved(effect);
				itr.remove();
				ret = true;
				livingEntity.effectsChanged = true;
			}
		}
		return ret;
	}
}
