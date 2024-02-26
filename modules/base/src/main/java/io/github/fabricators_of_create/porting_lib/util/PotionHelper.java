package io.github.fabricators_of_create.porting_lib.util;

import java.util.Iterator;

import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.LivingEntityAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PotionHelper {
	/***
	 * Removes all potion effects that have curativeItem as a curative item for its effect
	 * @param curativeItem The itemstack we are using to cure potion effects
	 */
	public static boolean curePotionEffects(LivingEntity livingEntity, ItemStack curativeItem) {
		if (livingEntity.level().isClientSide)
			return false;
		boolean ret = false;
		Iterator<MobEffectInstance> itr = livingEntity.getActiveEffects().iterator();
		while (itr.hasNext()) {
			MobEffectInstance effect = itr.next();
			if (effect.isCurativeItem(curativeItem) /*&& !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent(this, effect))*/) {
				((LivingEntityAccessor)livingEntity).port_lib$onEffectRemoved(effect);
				itr.remove();
				ret = true;
				livingEntity.effectsDirty = true;
			}
		}
		return ret;
	}
}
