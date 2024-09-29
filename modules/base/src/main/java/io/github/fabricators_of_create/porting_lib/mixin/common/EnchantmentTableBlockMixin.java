package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Iterator;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.util.RandomSource;

import net.minecraft.world.level.block.EnchantingTableBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantmentTableBlockMixin {

	@Inject(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
	public void port_lib$particles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos) {
		if (random.nextInt(16) == 0 && isValid(level, pos, blockPos)) {
			level.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)blockPos.getX() + random.nextFloat()) - 0.5D, (float)blockPos.getY() - random.nextFloat() - 1.0F, (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5D);
		}
	}

	private static boolean isValid(Level level, BlockPos blockPos, BlockPos blockPos2) {
		BlockState state = level.getBlockState(blockPos.offset(blockPos2));
		if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock)
			return bonusBlock.getEnchantPowerBonus(state, level, blockPos.offset(blockPos2)) != 0 && level.isEmptyBlock(blockPos.offset(blockPos2.getX() / 2, blockPos2.getY(), blockPos2.getZ() / 2));
		return false;
	}
}
