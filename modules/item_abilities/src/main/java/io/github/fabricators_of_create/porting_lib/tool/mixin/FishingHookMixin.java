package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityItem;
import net.minecraft.world.entity.projectile.FishingHook;

import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public class FishingHookMixin {
	@ModifyReceiver(method = "shouldStopFishing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private ItemStack toolActionCast(ItemStack instance, Item item) {
		if (instance.getItem() instanceof ItemAbilityItem) {
			if (instance.canPerformAction(ItemAbilities.FISHING_ROD_CAST)) {
				return instance.getItem() instanceof FishingRodItem ? instance : Items.FISHING_ROD.getDefaultInstance();
			}
			return Items.AIR.getDefaultInstance();
		}
		return instance;
	}
}
