package io.github.fabricators_of_create.porting_lib.item.mixin.client;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomFuelItem;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin {
	@Shadow
	@Final
	private RecipeType<? extends AbstractCookingRecipe> recipeType;

	@Inject(method = "isFuel", at = @At("HEAD"), cancellable = true)
	private void port_lib$tryUseCustomFuelItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem) {
			cir.setReturnValue(fuelItem.getBurnTime(stack, this.recipeType) > 0);
		}
	}
}
