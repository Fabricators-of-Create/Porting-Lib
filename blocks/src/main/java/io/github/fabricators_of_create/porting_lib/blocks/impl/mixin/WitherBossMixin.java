package io.github.fabricators_of_create.porting_lib.blocks.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.blocks.api.addons.EntityDestroyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Entity {

	public WitherBossMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	public boolean port_lib$canDestroy(boolean original, @Share("custom") LocalRef<Boolean> customLogic, @Share("shouldBreak") LocalRef<Boolean> shouldBreak) {
		if (customLogic.get())
			return shouldBreak.get();
		return original;
	}

	@Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$shouldDestroy(CallbackInfo ci, @Local(index = 11) BlockPos blockPos, @Share("custom") LocalRef<Boolean> customLogic, @Share("shouldBreak") LocalRef<Boolean> shouldBreak) {
		BlockState blockState = this.level().getBlockState(blockPos);
		if (blockState.getBlock() instanceof EntityDestroyBlock destroyBlock) {
			customLogic.set(true);
			shouldBreak.set(destroyBlock.canEntityDestroy(blockState, this.level(), blockPos, this));
		} else
			customLogic.set(false);
	}
}
