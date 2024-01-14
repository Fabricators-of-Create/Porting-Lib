package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface EntityDestroyBlock {
	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @return True to allow the ender dragon to destroy this block
	 */
	default boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		if (entity instanceof EnderDragon) {
			return !((Block)this).defaultBlockState().is(BlockTags.DRAGON_IMMUNE);
		} else if ((entity instanceof WitherBoss) ||
				(entity instanceof WitherSkull)) {
			return state.isAir() || WitherBoss.canDestroy(state);
		}

		return true;
	}
}
