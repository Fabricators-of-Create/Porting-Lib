package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.EnchantmentBonusBlock;
import net.minecraft.world.inventory.EnchantmentMenu;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
	@ModifyExpressionValue(
			method = "method_17411",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"
			)
	)
	private boolean modifyEnchantValue(boolean original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(ordinal = 1) BlockPos offset, @Local(ordinal = 0) LocalIntRef powerRef) {
		BlockState state = level.getBlockState(pos.offset(offset));
		if (state.getBlock() instanceof EnchantmentBonusBlock block) {
			powerRef.set(powerRef.get() + block.getEnchantPowerBonus(state, level, pos.offset(offset)));
			return false;
		}
		return original;
	}
}
