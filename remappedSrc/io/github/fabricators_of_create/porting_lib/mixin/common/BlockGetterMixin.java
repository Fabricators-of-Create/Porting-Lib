package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockView.class)
public interface BlockGetterMixin {
	@Shadow
	BlockState getBlockState(BlockPos pos);

	@Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
	default void port_lib$lightLevel(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		BlockState state = getBlockState(pos);
		if(state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			cir.setReturnValue(lightEmissiveBlock.getLightEmission(state, (BlockView) this, pos));
		}
	}
}
