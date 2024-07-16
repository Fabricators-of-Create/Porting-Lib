package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
	@ModifyVariable(
			method = "method_17411",
			at = @At(value = "STORE", ordinal = 1), ordinal = 0
	)
	private int modifyEnchantValue(int obj, ItemStack stack, Level level, BlockPos pos) {
		for (BlockPos blockPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
			BlockPos actualPos = pos.offset(blockPos);
			BlockState state = level.getBlockState(actualPos);
			if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock)
				obj += bonusBlock.getEnchantPowerBonus(state, level, actualPos);
		}
		return obj;
	}
}
