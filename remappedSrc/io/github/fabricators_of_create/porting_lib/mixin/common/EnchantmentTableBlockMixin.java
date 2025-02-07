package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.enchant.EnchantmentBonusBlock;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantmentTableBlockMixin {

	@Inject(method = "animateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$particles(BlockState state, World level, BlockPos pos, Random random, CallbackInfo ci, Iterator var5, BlockPos blockPos) {
		if (random.nextInt(16) == 0 && isValid(level, pos, blockPos)) {
			level.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)blockPos.getX() + random.nextFloat()) - 0.5D, (double)((float)blockPos.getY() - random.nextFloat() - 1.0F), (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5D);
		}
	}

	private static boolean isValid(World level, BlockPos blockPos, BlockPos blockPos2) {
		BlockState state = level.getBlockState(blockPos.add(blockPos2));
		if (state.getBlock() instanceof EnchantmentBonusBlock bonusBlock)
			return bonusBlock.getEnchantPowerBonus(state, level, blockPos.add(blockPos2)) != 0 && level.isAir(blockPos.add(blockPos2.getX() / 2, blockPos2.getY(), blockPos2.getZ() / 2));
		return false;
	}
}
