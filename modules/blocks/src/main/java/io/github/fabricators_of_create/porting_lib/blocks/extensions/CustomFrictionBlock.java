package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public interface CustomFrictionBlock {
	/**
	 * Gets the slipperiness at the given location at the given state. Normally
	 * between 0 and 1.
	 * <p>
	 * Note that entities may reduce slipperiness by a certain factor of their own;
	 * for {@link LivingEntity}, this is {@code .91}.
	 * {@link ItemEntity} uses {@code .98}, and
	 * {@link FishingHook} uses {@code .92}.
	 *
	 * @param state  state of the block
	 * @param level  the level
	 * @param pos    the position in the level
	 * @param entity the entity in question
	 * @return the factor by which the entity's motion should be multiplied
	 */
	default float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
		return ((Block) this).getFriction();
	}
}
