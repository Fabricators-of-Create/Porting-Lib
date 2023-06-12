package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
	@Inject(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void getDestroySpeed(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir, float f) {
		float original = player.getDestroySpeed(state);
		PlayerEvents.BreakSpeed breakSpeed = new PlayerEvents.BreakSpeed(player, state, original, pos);
		breakSpeed.sendEvent();
		float newSpeed = breakSpeed.getNewSpeed();
		if (newSpeed != original) {
			if (f == -1.0F) {
				cir.setReturnValue(0.0F);
			} else {
				int i = player.hasCorrectToolForDrops(state) ? 30 : 100;
				cir.setReturnValue(newSpeed / f / (float) i);
			}
		}
	}
}
