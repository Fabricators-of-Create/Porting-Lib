package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.ItemInHandRendererAccessor;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;

public class FirstPersonRendererHelper {
	public static ItemStack getStackInMainHand(HeldItemRenderer renderer) {
		return ((ItemInHandRendererAccessor) renderer).port_lib$getMainHandItem();
	}

	public static ItemStack getStackInOffHand(HeldItemRenderer renderer) {
		return ((ItemInHandRendererAccessor) renderer).port_lib$getOffHandItem();
	}
}
