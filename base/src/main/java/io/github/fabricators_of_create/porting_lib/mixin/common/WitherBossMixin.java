package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.block.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Entity {

	public WitherBossMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}
	@Unique
	private boolean customLogic = false;
	@Unique
	private boolean shouldBreak = false;

	@ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	public boolean port_lib$canDestroy(boolean original) {
		if (customLogic)
			return shouldBreak;
		return original;
	}

	@Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$shouldDestroy(CallbackInfo ci, int i, int j, int k, boolean bl, int l, int m, int n, int o, int p, int q, BlockPos blockPos) {
		BlockState blockState = this.level().getBlockState(blockPos);
		if (blockState.getBlock() instanceof EntityDestroyBlock destroyBlock) {
			customLogic = true;
			shouldBreak = destroyBlock.canEntityDestroy(blockState, this.level(), blockPos, this);
		} else
			customLogic = false;
	}
}
