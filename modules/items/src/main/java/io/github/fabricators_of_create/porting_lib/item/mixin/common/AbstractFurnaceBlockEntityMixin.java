package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomFuelItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
	@Unique private RecipeType<?> recipeType;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void port_lib$storeRecipeType(BlockEntityType<?> type, BlockPos pos, BlockState blockState, RecipeType<?> recipeType, CallbackInfo ci) {
		this.recipeType = recipeType;
	}

	@WrapOperation(method = "getBurnDuration", at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
	private <K, V> V port_lib$tryUseCustomFuel(Map<K, V> instance, Object key, V defaultValue, Operation<V> original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem) {
			return (V) (Object) fuelItem.getBurnTime(stack, this.recipeType); // yes, I understand that this is an unsafe cast. but the return type is in fact an int.
		}

		return original.call(instance, key, defaultValue);
	}

	@Inject(method = "isFuel", at = @At("HEAD"), cancellable = true)
	private static void port_lib$checkIsCustomFuel(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem) {
			cir.setReturnValue(fuelItem.getBurnTime(stack, null) > 0);
		}
	}

	@WrapOperation(method = "canPlaceItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;isFuel(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean port_lib$checkIsCustomFuel(ItemStack stack, Operation<Boolean> original) {
		if (stack.getItem() instanceof CustomFuelItem fuelItem) {
			return fuelItem.getBurnTime(stack, this.recipeType) > 0;
		}

		return original.call(stack);
	}
}
