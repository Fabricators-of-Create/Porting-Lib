package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
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
public class EnchantmentMenuMixin {
	@Group(name = "enchantValue", min = 1, max = 2)
	@ModifyVariable(method = "m_mpsetdhw", at = @At(value = "STORE", ordinal = 0), ordinal = 0, require = 0, remap = false)
	private int port_lib$modifyEnchantValueHashed(int obj, ItemStack stack, Level level, BlockPos pos) {
		return port_lib$modifyEnchantValueImpl(obj, stack, level, pos);
	}

	@Group(name = "enchantValue", min = 1, max = 2)
	@ModifyVariable(method = "method_17411", at = @At(value = "STORE", ordinal = 0), ordinal = 0, require = 0, remap = false)
	private int port_lib$modifyEnchantValueIntermediary(int obj, ItemStack stack, Level level, BlockPos pos) {
		return port_lib$modifyEnchantValueImpl(obj, stack, level, pos);
	}

	@Group(name = "enchantValue", min = 1, max = 2)
	@ModifyVariable(method = "lambda$slotsChanged$0", at = @At(value = "STORE", ordinal = 0), ordinal = 0, require = 0, remap = false)
	private int port_lib$modifyEnchantValueMojang(int obj, ItemStack stack, Level level, BlockPos pos) {
		return port_lib$modifyEnchantValueImpl(obj, stack, level, pos);
	}

	private int port_lib$modifyEnchantValueImpl(int obj, ItemStack stack, Level level, BlockPos pos) {
		for (BlockPos blockPos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
			if (level.getBlockState(pos.offset(blockPos)).getBlock() instanceof EnchantmentBonusBlock bonusBlock)
				obj += bonusBlock.getEnchantPowerBonus(level.getBlockState(pos.offset(blockPos)), level, pos.offset(blockPos));
		}
		return obj;
	}
}
