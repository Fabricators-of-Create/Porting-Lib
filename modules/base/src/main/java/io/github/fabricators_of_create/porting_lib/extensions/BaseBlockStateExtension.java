package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.block.BeaconColorMultiplierBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomSlimeBlock;
import io.github.fabricators_of_create.porting_lib.block.EntityDestroyBlock;
import io.github.fabricators_of_create.porting_lib.block.PlayerDestroyBlock;
import io.github.fabricators_of_create.porting_lib.block.StickToBlock;
import io.github.fabricators_of_create.porting_lib.block.StickyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * These extensions aren't implemented themselves and are only here so modders don't need to manually check for their mod compatible interfaces preventing.
 */
public interface BaseBlockStateExtension {
	/**
	 * @param level  The level
	 * @param pos    The position of this state
	 * @param beacon The position of the beacon
	 * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
	 */
	@Nullable
	default float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beacon) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof BeaconColorMultiplierBlock beaconColorMultiplierBlock)
			return beaconColorMultiplierBlock.getBeaconColorMultiplier((BlockState) this, level, pos, beacon);
		if (block instanceof BeaconBeamBlock beamBlock)
			return beamBlock.getColor().getTextureDiffuseColors();
		return null;
	}

	/**
	 * Called when a player removes a block.  This is responsible for
	 * actually destroying the block, and the block is intact at time of call.
	 * This is called regardless of whether the player can harvest the block or
	 * not.
	 * <p>
	 * Return true if the block is actually destroyed.
	 * <p>
	 * Note: When used in multiplayer, this is called on both client and
	 * server sides!
	 *
	 * @param level       The current level
	 * @param player      The player damaging the block, may be null
	 * @param pos         Block position in level
	 * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
	 *                    Can be useful to delay the destruction of tile entities till after harvestBlock
	 * @param fluid       The current fluid and block state for the position in the level.
	 * @return True if the block is actually destroyed.
	 */
	default boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof PlayerDestroyBlock destroyBlock)
			return destroyBlock.onDestroyedByPlayer((BlockState) this, level, pos, player, willHarvest, fluid);

		block.playerWillDestroy(level, pos, (BlockState) this, player);
		return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
	}

	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param level The current level
	 * @param pos   Block position in level
	 * @return True to allow the ender dragon to destroy this block
	 */
	default boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof EntityDestroyBlock destroyBlock)
			return destroyBlock.canEntityDestroy((BlockState) this, level, pos, entity);

		if (entity instanceof EnderDragon) {
			return !block.defaultBlockState().is(BlockTags.DRAGON_IMMUNE);
		} else if ((entity instanceof WitherBoss) ||
				(entity instanceof WitherSkull)) {
			return ((BlockState) this).isAir() || WitherBoss.canDestroy((BlockState) this);
		}

		return true;
	}

	/**
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	default boolean isSlimeBlock() {
		if (((BlockState) this).getBlock() instanceof CustomSlimeBlock slimeBlock)
			return slimeBlock.isSlimeBlock((BlockState) this);
		return ((BlockState) this).is(Blocks.SLIME_BLOCK);
	}

	/**
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	default boolean isStickyBlock() {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof StickyBlock stickyBlock)
			return stickyBlock.isStickyBlock((BlockState) this);
		return block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK;
	}

	/**
	 * Determines if this block can stick to another block when pushed by a piston.
	 *
	 * @param other Other block
	 * @return True to link blocks
	 */
	default boolean canStickTo(@NotNull BlockState other) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof StickToBlock stickTo)
			return stickTo.canStickTo((BlockState) this, other);
		if (block == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) return false;
		if (block == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) return false;
		return isStickyBlock() || other.isStickyBlock();
	}
}
