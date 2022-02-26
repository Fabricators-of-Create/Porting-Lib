package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.item.EntityTickListenerItem;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void port_lib$onHeadTick(CallbackInfo ci) {
		ItemEntity self = (ItemEntity) (Object) this;
		ItemStack stack = self.getItem();
		if (stack.getItem() instanceof EntityTickListenerItem && ((EntityTickListenerItem) stack.getItem()).onEntityItemUpdate(stack, self)) {
			ci.cancel();
		}
	}
}
