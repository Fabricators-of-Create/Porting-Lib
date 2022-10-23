package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
	@ModifyVariable(
			method = { "method_17411", "m_mpsetdhw", "lambda$slotsChanged$0" },
			at = @At(value = "STORE", ordinal = 0), ordinal = 0, remap = false
	)
	private int port_lib$modifyEnchantValue(int obj, ItemStack stack, Level level, BlockPos pos) {
		for (BlockPos blockPos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
			BlockPos actualPos = pos.offset(blockPos);
			BlockState state = level.getBlockState(actualPos);
			if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock)
				obj += bonusBlock.getEnchantPowerBonus(state, level, actualPos);
		}
		return obj;
	}
}
