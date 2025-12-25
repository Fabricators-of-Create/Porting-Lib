package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomFuelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
	private RecipeType<?> port_lib$recipeType;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void storeRecipeType(BlockEntityType<?> type, BlockPos pos, BlockState blockState, RecipeType<?> recipeType, CallbackInfo ci) {
		this.port_lib$recipeType = recipeType;
	}

	@WrapOperation(method = "getBurnDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/FuelValues;burnDuration(Lnet/minecraft/world/item/ItemStack;)I"))
	private int tryUseCustomFuel(FuelValues instance, ItemStack stack, Operation<Integer> original) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem)
			return fuelItem.getBurnTime(stack, this.port_lib$recipeType, instance);

		return original.call(instance, stack);
	}

	@WrapOperation(method = "canPlaceItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/FuelValues;isFuel(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean checkIsCustomFuel(FuelValues instance, ItemStack stack, Operation<Boolean> original) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem)
			return fuelItem.getBurnTime(stack, this.port_lib$recipeType, instance) > 0;

		return original.call(instance, stack);
	}
}
