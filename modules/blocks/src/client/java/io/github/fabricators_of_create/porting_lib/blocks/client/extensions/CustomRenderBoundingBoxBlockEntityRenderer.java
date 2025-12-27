package io.github.fabricators_of_create.porting_lib.blocks.client.extensions;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CustomRenderBoundingBoxBlockEntityRenderer<T extends BlockEntity> {
	/**
	 * Return an {@link AABB} that controls the visible scope of this {@link BlockEntityRenderer}.
	 * Defaults to the unit cube at the given position. {@link io.github.fabricators_of_create.porting_lib.blocks.BlockHooks#INFINITE_AABB} can be used to declare the BER
	 * should be visible everywhere.
	 *
	 * @return an appropriately sized {@link AABB} for the {@link BlockEntityRenderer}
	 */
	default AABB getRenderBoundingBox(T blockEntity) {
		return new AABB(blockEntity.getBlockPos());
	}
}
