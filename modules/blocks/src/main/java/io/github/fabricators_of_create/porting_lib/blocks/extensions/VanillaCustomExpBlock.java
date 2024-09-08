package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Same as {@link CustomExpBlock} but doesn't override vanilla functionality
 */
public interface VanillaCustomExpBlock {
	/**
	 * Returns how many experience points this block drops when broken, before application of {@linkplain EnchantmentEffectComponents#BLOCK_EXPERIENCE enchantments}.
	 *
	 * @param state       The state of the block being broken
	 * @param level       The level
	 * @param pos         The position of the block being broken
	 * @param blockEntity The block entity, if any
	 * @param breaker     The entity who broke the block, if known
	 * @param tool        The item stack used to break the block. May be empty
	 * @return The amount of experience points dropped by this block
	 */
	default int port_lib$getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
		return 0;
	}
}
