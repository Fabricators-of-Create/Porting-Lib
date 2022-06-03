package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
	@ModifyVariable(method = "m_mpsetdhw", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	private int port_lib$modifyEnchantValue(int obj, ItemStack stack, Level level, BlockPos pos) {
		for(BlockPos blockPos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
			if (level.getBlockState(pos.offset(blockPos)).getBlock() instanceof EnchantmentBonusBlock bonusBlock)
				obj += bonusBlock.getEnchantPowerBonus(level.getBlockState(pos.offset(blockPos)), level, pos.offset(blockPos));
		}
		return obj;
	}
}
