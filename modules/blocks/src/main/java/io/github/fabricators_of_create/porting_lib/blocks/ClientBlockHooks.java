package io.github.fabricators_of_create.porting_lib.blocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public class ClientBlockHooks {
	public static boolean isBlockInSolidLayer(BlockState state) {
		return ItemBlockRenderTypes.getChunkRenderType(state) != RenderType.solid();
	}

	// Quite lovely hacks.... said no one
	public static final ThreadLocal<BlockGetter> lightEngineLevelContextHack = new ThreadLocal<>();
	public static final ThreadLocal<BlockPos> lightEngineBlockPosContextHack = new ThreadLocal<>();

	public static boolean hasDifferentLightProperties(BlockGetter level, BlockPos pos, BlockState state1, BlockState state2) {
		lightEngineLevelContextHack.set(level);
		lightEngineBlockPosContextHack.set(pos);
		return LightEngine.hasDifferentLightProperties(state1, state2);
	}
}
