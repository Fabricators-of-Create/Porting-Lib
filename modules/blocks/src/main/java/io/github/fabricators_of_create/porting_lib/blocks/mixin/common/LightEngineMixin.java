package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.ClientBlockHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.LightEmissiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public class LightEngineMixin {
	@WrapOperation(method = "hasDifferentLightProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
	private static int customLightEmissionBlock(BlockState state, Operation<Integer> operation) {
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock)
			return lightEmissiveBlock.getLightEmission(state, ClientBlockHooks.lightEngineLevelContextHack.get(), ClientBlockHooks.lightEngineBlockPosContextHack.get());
		return operation.call(state);
	}

	@Inject(method = "hasDifferentLightProperties", at = @At("TAIL"))
	private static void voidContexts(BlockState state1, BlockState state2, CallbackInfoReturnable<Boolean> cir) {
		ClientBlockHooks.lightEngineLevelContextHack.remove();
		ClientBlockHooks.lightEngineBlockPosContextHack.remove();
	}
}
