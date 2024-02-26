package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.block.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.WitherSkull;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkull.class)
public class WitherSkullMixin {
	@Unique
	private boolean customLogic = false;
	@Unique
	private boolean shouldBreak = false;

	@Inject(method = "getBlockExplosionResistance", at = @At("HEAD"))
	public void port_lib$canDestroy(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f, CallbackInfoReturnable<Float> cir) {
		if (blockState.getBlock() instanceof EntityDestroyBlock destroyBlock) {
			customLogic = true;
			shouldBreak = destroyBlock.canEntityDestroy(blockState, blockGetter, blockPos, (Entity) (Object) this);
		} else
			customLogic = false;
	}

	@ModifyExpressionValue(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/WitherSkull;isDangerous()Z"))
	public boolean port_lib$canDestroy(boolean original) {
		if (customLogic)
			return original && shouldBreak;
		return original;
	}
}
