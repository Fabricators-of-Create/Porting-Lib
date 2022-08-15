package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.item.BundleItem;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BundleItem.class)
public interface BundleItemAccessor {
	@Invoker("getWeight")
	static int port_lib$getWeight(ItemStack stack) {
		throw new RuntimeException("mixin failed");
	}
}
