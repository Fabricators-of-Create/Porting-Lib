package io.github.fabricators_of_create.porting_lib.blocks.injects;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.BeaconColorMultiplierBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomSlimeBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.EntityDestroyBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.FaceHidingBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.OnTreeGrowBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.PlayerDestroyBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.StateViewpointBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.StickToBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.StickyBlock;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * These extensions aren't implemented themselves and are only here so modders don't need to manually check for their mod compatible interfaces preventing.
 */
public interface BlockStateInjection {
	/**
	 * @param level  The level
	 * @param pos    The position of this state
	 * @param beacon The position of the beacon
	 * @return An int RGB to be averaged with a beacon's existing beam color, or original to do nothing to the beam
	 */

	default int port_lib$getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beacon, int original) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof BeaconColorMultiplierBlock beaconColorMultiplierBlock)
			return beaconColorMultiplierBlock.getBeaconColorMultiplier((BlockState) this, level, pos, beacon, original);
		if (block instanceof BeaconBeamBlock beamBlock)
			return beamBlock.getColor().getTextureDiffuseColor();
		return original;
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
	default boolean port_lib$onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
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
	default boolean port_lib$canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
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
	default boolean port_lib$isSlimeBlock() {
		if (((BlockState) this).getBlock() instanceof CustomSlimeBlock slimeBlock)
			return slimeBlock.isSlimeBlock((BlockState) this);
		return ((BlockState) this).is(Blocks.SLIME_BLOCK);
	}

	/**
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	default boolean port_lib$isStickyBlock() {
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
	default boolean port_lib$canStickTo(@NotNull BlockState other) {
		Block block = ((BlockState) this).getBlock();
		if (block instanceof StickToBlock stickTo)
			return stickTo.canStickTo((BlockState) this, other);
		if (block == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) return false;
		if (block == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) return false;
		return port_lib$isStickyBlock() || other.port_lib$isStickyBlock();
	}

	/**
	 * Called when a tree grows on top of this block and tries to set it to dirt by the trunk placer.
	 * An override that returns true is responsible for using the place function to
	 * set blocks in the world properly during generation. A modded grass block might override this method
	 * to ensure it turns into the corresponding modded dirt instead of regular dirt when a tree grows on it.
	 * For modded grass blocks, returning true from this method is NOT a substitute for adding your block
	 * to the #minecraft:dirt tag, rather for changing the behaviour to something other than setting to dirt.
	 *
	 * NOTE: This happens DURING world generation, the generation may be incomplete when this is called.
	 * Use the placeFunction when modifying the level.
	 *
	 * @param level         The current level
	 * @param placeFunction Function to set blocks in the level for the tree, use this instead of the level directly
	 * @param randomSource  The random source
	 * @param pos           Position of the block to be set to dirt
	 * @param config        Configuration of the trunk placer. Consider azalea trees, which should place rooted dirt instead of regular dirt.
	 * @return True to ignore vanilla behaviour
	 */
	default boolean port_lib$onTreeGrow(LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		if (((BlockState) this).getBlock() instanceof OnTreeGrowBlock block)
			return block.onTreeGrow((BlockState) this, level, placeFunction, randomSource, pos, config);
		return false;
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link Camera#port_lib$getBlockAtCamera()}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 *
	 * @param level     the level
	 * @param pos       the position
	 * @param viewpoint the viewpoint
	 * @return the block state that should be 'seen'
	 */
	default BlockState port_lib$getStateAtViewpoint(BlockGetter level, BlockPos pos, Vec3 viewpoint) {
		if (((BlockState)this).getBlock() instanceof StateViewpointBlock block)
			return block.getStateAtViewpoint((BlockState) this, level, pos, viewpoint);
		return (BlockState) this;
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 * <p>
	 * This method should only be used for blocks you don't control, for your own blocks override
	 * {@link net.minecraft.world.level.block.Block#skipRendering(BlockState, BlockState, Direction)}
	 * on the respective block instead
	 *
	 * @param level The world
	 * @param pos The blocks position in the world
	 * @param neighborState The neighboring blocks {@link BlockState}
	 * @param dir The direction towards the neighboring block
	 */
	default boolean port_lib$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
		if (((BlockState) this).getBlock() instanceof FaceHidingBlock block)
			return block.hidesNeighborFace(level, pos, ((BlockState)this), neighborState, dir);
		return false;
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link BlockStateInjection#port_lib$hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean port_lib$supportsExternalFaceHiding() {
		if (((BlockState)this).getBlock() instanceof FaceHidingBlock block)
			return block.supportsExternalFaceHiding((BlockState) this);
		return false;
	}
}
