package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CustomBoundingBoxBlockEntityRenderer {
	AABB INFINITE_EXTENT_AABB = new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

	/**
	 * Return an {@link AABB} that controls the visible scope of this {@link BlockEntityRenderer}.
	 * Defaults to the unit cube at the given position. {@link CustomBoundingBoxBlockEntityRenderer#INFINITE_EXTENT_AABB} can be used to declare the BER
	 * should be visible everywhere.
	 *
	 * @return an appropriately sized {@link AABB} for the {@link BlockEntityRenderer}
	 */
	default <T extends BlockEntity> AABB getRenderBoundingBox(T blockEntity) {
		AABB box = getInfiniteBoundingBox();
		BlockPos pos = blockEntity.getBlockPos();
		try {
			VoxelShape collisionShape = blockEntity.getBlockState().getCollisionShape(blockEntity.getLevel(), pos);
			if (!collisionShape.isEmpty()) {
				box = collisionShape.bounds().move(pos);
			}
		} catch (Exception e) {
			box = AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 1, 1));
		}

		return box;
	}

	default AABB getInfiniteBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
