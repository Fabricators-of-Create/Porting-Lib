package io.github.fabricators_of_create.porting_lib.test;

import io.github.fabricators_of_create.porting_lib.block.CustomRenderBoundingBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class StrangeChestBlockEntity extends ChestBlockEntity implements CustomRenderBoundingBoxBlockEntity {
	protected StrangeChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(PortingLibTest.STRANGE_CHEST_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public AABB getRenderBoundingBox() {
		RandomSource randomSource = this.level.getRandom();
		return CustomRenderBoundingBoxBlockEntity.super.getRenderBoundingBox().move(randomSource.nextInt(10), randomSource.nextInt(10), randomSource.nextInt(10));
	}
}
