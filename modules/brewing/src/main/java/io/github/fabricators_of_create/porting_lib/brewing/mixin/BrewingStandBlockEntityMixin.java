package io.github.fabricators_of_create.porting_lib.brewing.mixin;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {
//	@Shadow
//	@Final
//	private static int[] SLOTS_FOR_SIDES;
//
//	@Shadow
//	public abstract ItemStack getItem(int i);
//
//	@Inject(method = "isBrewable", at = @At("HEAD"), cancellable = true)
//	private static void port_lib$customBrew(NonNullList<ItemStack> inputs, CallbackInfoReturnable<Boolean> cir) {
//		ItemStack itemStack = inputs.get(3);
//		if (!itemStack.isEmpty()) {
//			// Leave vanilla brewing recipes alone so we don't fuck with other mods
//			boolean canBrew = BrewingRecipeRegistry.canBrew(inputs, itemStack, SLOTS_FOR_SIDES);
//			if (canBrew)
//				cir.setReturnValue(true);
//		}
//	}
//
//	@Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
//	private static void port_lib$customBrewLogic(Level level, BlockPos blockPos, NonNullList<ItemStack> inputs, CallbackInfo ci) {
//		// Check if we are doing a modded brew
//		if (BrewingRecipeRegistry.canBrew(inputs, inputs.get(3), SLOTS_FOR_SIDES)) {
//			BrewingHandler.doBrew(level, blockPos, inputs, SLOTS_FOR_SIDES);
//			ci.cancel();
//		}
//	}
//
//	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
//	public void port_lib$validBrewItem(int i, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
//		if (i == 3) {
//			if (BrewingRecipeRegistry.isValidIngredient(itemStack))
//				cir.setReturnValue(true);
//			else if (i != 4) {
//				if (BrewingRecipeRegistry.isValidInput(itemStack) && this.getItem(i).isEmpty())
//					cir.setReturnValue(true);
//			}
//		}
//	}
}
