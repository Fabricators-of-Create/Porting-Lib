package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.tags.TagKey;

import net.minecraft.world.level.block.EnchantingTableBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.EnchantmentBonusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantmentTableBlockMixin {
	@WrapOperation(method = "isValidBookShelf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
	private static boolean checkIsValid(BlockState state, TagKey<BlockState> tagKey, Operation<Boolean> original, Level level, BlockPos enchantingTablePos, BlockPos bookshelfPos) {
		if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock) {
			return bonusBlock.getEnchantPowerBonus(state, level, enchantingTablePos.offset(bookshelfPos)) != 0;
		}

		return original.call(state, tagKey);
	}
}
