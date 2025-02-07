package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface EntityDestroyBlock {
	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @return True to allow the ender dragon to destroy this block
	 */
	default boolean canEntityDestroy(BlockState state, BlockView level, BlockPos pos, Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			return !((Block)this).getDefaultState().isIn(BlockTags.DRAGON_IMMUNE);
		} else if ((entity instanceof WitherEntity) ||
				(entity instanceof WitherSkullEntity)) {
			return state.isAir() || WitherEntity.canDestroy(state);
		}

		return true;
	}
}
