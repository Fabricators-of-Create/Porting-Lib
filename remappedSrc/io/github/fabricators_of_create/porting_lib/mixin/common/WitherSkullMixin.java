package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.util.EntityDestroyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

@Mixin(WitherSkullEntity.class)
public class WitherSkullMixin {
	@Unique
	private boolean customLogic = false;
	@Unique
	private boolean shouldBreak = false;

	@Inject(method = "getBlockExplosionResistance", at = @At("HEAD"))
	public void port_lib$canDestroy(Explosion explosion, BlockView blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f, CallbackInfoReturnable<Float> cir) {
		if (blockState.getBlock() instanceof EntityDestroyBlock destroyBlock) {
			customLogic = true;
			shouldBreak = destroyBlock.canEntityDestroy(blockState, blockGetter, blockPos, (Entity) (Object) this);
		}
	}

	@ModifyExpressionValue(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/WitherSkull;isDangerous()Z"))
	public boolean port_lib$canDestroy(boolean original) {
		if (customLogic)
			return original && shouldBreak;
		return original;
	}
}
