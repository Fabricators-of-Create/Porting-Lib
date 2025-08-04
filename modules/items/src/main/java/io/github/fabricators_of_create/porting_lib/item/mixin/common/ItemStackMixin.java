package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomDamageItem;
import io.github.fabricators_of_create.porting_lib.item.injects.ItemStackInjection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackInjection {
	@Shadow
	public abstract Item getItem();

	@ModifyVariable(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("HEAD"), argsOnly = true)
	private int port_lib$tryUseCustomDamage(int value, @Local(argsOnly = true) ServerPlayer entity, @Local(argsOnly = true) Consumer<Item> onBroken) {
		if (this.getItem() instanceof CustomDamageItem customDamageItem) {
			return customDamageItem.damageItem((ItemStack) (Object) this, value, entity, onBroken);
		}

		return value;
	}
}
