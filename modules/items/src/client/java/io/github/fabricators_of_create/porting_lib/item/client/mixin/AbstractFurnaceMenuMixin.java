package io.github.fabricators_of_create.porting_lib.item.client.mixin;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomFuelItem;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraft.world.level.Level;

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
	protected Level level;

	@Shadow
	@Final
	private RecipeType<? extends AbstractCookingRecipe> recipeType;

	@Inject(method = "isFuel", at = @At("HEAD"), cancellable = true)
	private void tryUseCustomFuelItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem) {
			cir.setReturnValue(fuelItem.getBurnTime(stack, this.recipeType, this.level.fuelValues()) > 0);
		}
	}
}
