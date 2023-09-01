package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.EntityTickListenerItem;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
