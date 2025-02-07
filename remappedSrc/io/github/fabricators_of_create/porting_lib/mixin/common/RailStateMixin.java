package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.fabricators_of_create.porting_lib.block.SlopeCreationCheckingRailBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(RailPlacementHelper.class)
public abstract class RailStateMixin {

	@Final
	@Shadow
	private AbstractRailBlock block;

	@Shadow
	private BlockState state;

	@Shadow
	@Final
	private World level;

	@Redirect(method = { "connectTo", "place" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseRailBlock;isRail(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean port_lib$redirectRailChecksToCheckSlopes(World level, BlockPos pos) {
		boolean canMakeSlopes = true;
		if (block instanceof SlopeCreationCheckingRailBlock checking) {
			canMakeSlopes = checking.canMakeSlopes(state, this.level, pos);
		}
		return AbstractRailBlock.isRail(level, pos) && canMakeSlopes;
	}

}
