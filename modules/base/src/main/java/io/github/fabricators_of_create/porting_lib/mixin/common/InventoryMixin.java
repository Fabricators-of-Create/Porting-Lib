package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.ArmorTickListeningItem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Mixin(Inventory.class)
public class InventoryMixin {
	@Shadow
	@Final
	public Player player;

	@Inject(method = "tick", at = @At("HEAD"))
	private void port_lib$inventoryTick(CallbackInfo ci) {
		player.getArmorSlots().forEach(stack -> {
			if(stack.getItem() instanceof ArmorTickListeningItem item) {
				item.onArmorTick(stack, player.level(), player);
			}
		});
	}
}
