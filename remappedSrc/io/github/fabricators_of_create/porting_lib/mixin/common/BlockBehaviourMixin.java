package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.common.PlayerBreakSpeedCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.class)
public abstract class BlockBehaviourMixin {
	@Inject(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void getDestroySpeed(BlockState state, PlayerEntity player, BlockView level, BlockPos pos, CallbackInfoReturnable<Float> cir, float f) {
		float original = player.getBlockBreakingSpeed(state);
		PlayerBreakSpeedCallback.BreakSpeed speed = new PlayerBreakSpeedCallback.BreakSpeed(player, state, original, pos);
		PlayerBreakSpeedCallback.EVENT.invoker().setBreakSpeed(speed);
		float newSpeed = speed.newSpeed;
		if (newSpeed != original) {
			if (f == -1.0F) {
				cir.setReturnValue(0.0F);
			} else {
				int i = player.canHarvest(state) ? 30 : 100;
				cir.setReturnValue(newSpeed / f / (float) i);
			}
		}
	}
}
