package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.CustomMaxCountItem;
import io.github.fabricators_of_create.porting_lib.item.api.common.addons.DamageableItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	public void port_lib$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

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
