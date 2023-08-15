package io.github.fabricators_of_create.porting_lib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StrangeChestBlock extends ChestBlock {
	public StrangeChestBlock(Properties properties) {
		super(properties, () -> PortingLibTest.STRANGE_CHEST_BLOCK_ENTITY_TYPE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StrangeChestBlockEntity(pos, state);
	}
}
