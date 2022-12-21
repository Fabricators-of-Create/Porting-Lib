package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
	// checks light != 0
	// to make it true, return a state that will 100% not be 0
	@WrapOperation(
			method = "read",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private static BlockState port_lib$customLight(ChunkAccess access, BlockPos pos, Operation<BlockState> original) {
		BlockState state = original.call(access, pos);
		if (state.getBlock() instanceof LightEmissiveBlock custom) {
			if (custom.getLightEmission(state, access, pos) != 0) {
				return Blocks.LIGHT.defaultBlockState();
			}
		}
		return state;
	}
}
