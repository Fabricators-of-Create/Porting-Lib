package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.item.extensions.EntityTickListenerItem;

import io.github.fabricators_of_create.porting_lib.item.extensions.OnDestroyedItem;

import net.minecraft.world.damagesource.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
	public void onHeadTick(CallbackInfo ci) {
		ItemStack stack = getItem();
		if (stack.getItem() instanceof EntityTickListenerItem listener && listener.onEntityItemUpdate(stack, (ItemEntity) (Object) this)) {
			ci.cancel();
		}
	}

	@WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onDestroyed(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
	private void onDestroyed(ItemStack instance, ItemEntity itemEntity, Operation<Void> original, DamageSource source) {
		if (instance.getItem() instanceof OnDestroyedItem onDestroyedItem) {
			onDestroyedItem.onDestroyed(itemEntity, source);
		} else {
			original.call(instance, itemEntity);
		}
	}
}
