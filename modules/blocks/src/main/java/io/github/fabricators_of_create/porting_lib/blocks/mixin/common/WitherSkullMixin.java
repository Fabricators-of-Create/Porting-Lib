package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.WitherSkull;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherSkull.class)
public class WitherSkullMixin {
	@WrapOperation(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	public boolean canDestroy(BlockState state, Operation<Boolean> original, Explosion explosion, BlockGetter level, BlockPos pos) {
		if (state.getBlock() instanceof EntityDestroyBlock destroyBlock) {
			return destroyBlock.canEntityDestroy(state, level, pos, (Entity) (Object) this);
		}
		return original.call(state);
	}
}
