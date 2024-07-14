package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.util.MutableDataComponentHolder;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib.item.DamageableItem;

import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MutableDataComponentHolder, ItemStackExtensions {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemSetDamage(int damage, CallbackInfo ci) {
		if(getItem() instanceof DamageableItem damagableItem) {
			damagableItem.setDamage((ItemStack) (Object) this, damage);
			ci.cancel();
		}
	}

	@Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getMaxDamage((ItemStack) (Object) this));
		}
	}

	@Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getDamage((ItemStack) (Object) this));
		}
	}
}
