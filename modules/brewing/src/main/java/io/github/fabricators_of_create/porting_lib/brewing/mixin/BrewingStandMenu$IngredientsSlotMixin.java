package io.github.fabricators_of_create.porting_lib.brewing.mixin;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot")
public class BrewingStandMenu$IngredientsSlotMixin {
	@Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
	public void port_lib$canPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (BrewingRecipeRegistry.isValidIngredient(itemStack))
			cir.setReturnValue(true);
	}
}
