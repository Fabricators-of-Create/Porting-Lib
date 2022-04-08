package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.PotionHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {
	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	public void port_lib$clearMobEffects(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
		if (!level.isClientSide) PotionHelper.curePotionEffects(livingEntity, stack);
	}
}
