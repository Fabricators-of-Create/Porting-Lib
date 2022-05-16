package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// allows block modification to be done in transactions easily.
// this only modifies set/getBlockState.
public interface LevelExtensions {
	default SnapshotParticipant<LevelSnapshotData> snapshotParticipant() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void updateSnapshots(TransactionContext ctx) {
		snapshotParticipant().updateSnapshots(ctx);
	}

	record LevelSnapshotData(List<ChangedPosData> changedStates) {
		public LevelSnapshotData(List<ChangedPosData> changedStates) {
			this.changedStates = changedStates == null ? null : new LinkedList<>(changedStates);
		}
	}

	record ChangedPosData(BlockPos pos, BlockState state, int flags) {
	}

	/**
	 * All part entities in this world. Used when collecting entities in an AABB to fix parts being
	 * ignored whose parent entity is in a chunk that does not intersect with the AABB.
	 */
	default Collection<PartEntity<?>> getPartEntities() {
		return getPartEntityMap().values();
	}

	default Int2ObjectMap<PartEntity<?>> getPartEntityMap() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void addFreshBlockEntities(Collection<BlockEntity> beList) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
