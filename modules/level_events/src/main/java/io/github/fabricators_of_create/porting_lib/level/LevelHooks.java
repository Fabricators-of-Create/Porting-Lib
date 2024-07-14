package io.github.fabricators_of_create.porting_lib.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.level.events.BlockEvent;
import io.github.fabricators_of_create.porting_lib.level.events.LevelEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;

import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class LevelHooks {
	public static boolean onCreateWorldSpawn(Level level, ServerLevelData settings) {
		return new LevelEvent.CreateSpawnPosition(level, settings).post();
	}

	private static final WeightedRandomList<MobSpawnSettings.SpawnerData> NO_SPAWNS = WeightedRandomList.create();

	public static WeightedRandomList<MobSpawnSettings.SpawnerData> getPotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList) {
		LevelEvent.PotentialSpawns event = new LevelEvent.PotentialSpawns(level, category, pos, oldList);
		if (event.post())
			return NO_SPAWNS;
		else if (event.getSpawnerDataList() == oldList.unwrap())
			return oldList;
		return WeightedRandomList.create(event.getSpawnerDataList());
	}

	/**
	 * Fires {@link BlockEvent.BreakEvent}, pre-emptively canceling the event based on the conditions that will cause the block to not be broken anyway.
	 * <p>
	 * Note that undoing the pre-cancel will not permit breaking the block, since the vanilla conditions will always be checked.
	 *
	 * @param level    The level
	 * @param gameType The game type of the breaking player
	 * @param player   The breaking player
	 * @param pos      The position of the block being broken
	 * @param state    The state of the block being broken
	 * @return The event
	 */
	public static BlockEvent.BreakEvent fireBlockBreak(Level level, GameType gameType, ServerPlayer player, BlockPos pos, BlockState state) {
		return fireBlockBreak(level, gameType, player, pos, state, args -> {
			return ((Item) args[0]).canAttackBlock((BlockState) args[1], (Level) args[2], (BlockPos) args[3], (Player) args[4]);
		});
	}

	public static BlockEvent.BreakEvent fireBlockBreak(Level level, GameType gameType, ServerPlayer player, BlockPos pos, BlockState state, Operation<Boolean> original) {
		boolean preCancelEvent = false;

		ItemStack itemstack = player.getMainHandItem();
		if (!itemstack.isEmpty() && !original.call(itemstack.getItem(), state, level, pos, player)) {
			preCancelEvent = true;
		}

		if (player.blockActionRestricted(level, pos, gameType)) {
			preCancelEvent = true;
		}

		if (state.getBlock() instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
			preCancelEvent = true;
		}

		// Post the block break event
		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
		event.setCanceled(preCancelEvent);
		event.sendEvent();

		// If the event is canceled, let the client know the block still exists
		if (event.isCanceled()) {
			player.connection.send(new ClientboundBlockUpdatePacket(pos, state));
		}

		return event;
	}

	public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
		BlockSnapshot snap = blockSnapshots.get(0);
		BlockState placedAgainst = snap.getLevel().getBlockState(snap.getPos().relative(direction.getOpposite()));
		BlockEvent.EntityMultiPlaceEvent event = new BlockEvent.EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
		return event.post();
	}

	public static boolean onBlockPlace(@Nullable Entity entity, BlockSnapshot blockSnapshot, Direction direction) {
		BlockState placedAgainst = blockSnapshot.getLevel().getBlockState(blockSnapshot.getPos().relative(direction.getOpposite()));
		BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
		return event.post();
	}

	public static BlockEvent.NeighborNotifyEvent onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
		BlockEvent.NeighborNotifyEvent event = new BlockEvent.NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate);
		event.sendEvent();
		return event;
	}

	public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
		BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state);
		event.sendEvent();
		return event.getNewState();
	}
}
