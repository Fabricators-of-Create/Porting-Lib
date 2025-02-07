package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentMenuMixin {
	@ModifyVariable(
			method = { "m_mpsetdhw", "method_17411", "lambda$slotsChanged$0" },
			at = @At(value = "STORE", ordinal = 0), ordinal = 0, remap = false
	)
	private int port_lib$modifyEnchantValue(int obj, ItemStack stack, World level, BlockPos pos) {
		for (BlockPos blockPos : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
			BlockPos actualPos = pos.add(blockPos);
			BlockState state = level.getBlockState(actualPos);
			if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock)
				obj += bonusBlock.getEnchantPowerBonus(state, level, actualPos);
		}
		return obj;
	}
}
