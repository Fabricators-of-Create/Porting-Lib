package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.StickToBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.StickyBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
	@Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
	private static void customIsSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof StickyBlock stickyBlock)
			cir.setReturnValue(stickyBlock.isStickyBlock(state));
	}

	@Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
	private static void customCanStickTo(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir) {
		boolean useCustomLogic = false;
		boolean canStickTo = state.port_lib$canStickTo(adjacentState);
		boolean canStickToAdj = adjacentState.port_lib$canStickTo(state);
		if (state.getBlock() instanceof StickToBlock stick)
			useCustomLogic = true;
		if (adjacentState.getBlock() instanceof StickToBlock stick)
			useCustomLogic = true;
		if (useCustomLogic)
			cir.setReturnValue(canStickTo && canStickToAdj);
	}
}
