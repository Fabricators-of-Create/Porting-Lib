package io.github.fabricators_of_create.porting_lib.brewing.mixin;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class BrewingStandMenu$PotionSlotMixin {
	@Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
	private static void port_lib$isValidInput(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (BrewingRecipeRegistry.isValidInput(itemStack))
			cir.setReturnValue(true);
	}
}
