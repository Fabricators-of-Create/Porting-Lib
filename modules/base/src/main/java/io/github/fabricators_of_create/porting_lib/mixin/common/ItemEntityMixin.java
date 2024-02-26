package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.item.EntityTickListenerItem;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
	public void port_lib$onHeadTick(CallbackInfo ci) {
		ItemStack stack = getItem();
		if (stack.getItem() instanceof EntityTickListenerItem listener && listener.onEntityItemUpdate(stack, (ItemEntity) (Object) this)) {
			ci.cancel();
		}
	}
}
