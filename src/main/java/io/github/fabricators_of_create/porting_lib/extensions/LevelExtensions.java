package io.github.fabricators_of_create.porting_lib.extensions;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// allows block modification to be done in transactions easily.
// this only modifies set/getBlockState.
public interface LevelExtensions {
	default SnapshotParticipant<LevelSnapshotData> snapshotParticipant() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void updateSnapshots(TransactionContext ctx) {
		snapshotParticipant().updateSnapshots(ctx);
	}

	record LevelSnapshotData(Map<BlockPos, ChangedPosData> changedStates) {
		public LevelSnapshotData(Map<BlockPos, ChangedPosData> changedStates) {
			this.changedStates = changedStates == null ? null : new HashMap<>(changedStates);
		}
	}

	record ChangedPosData(BlockState state, int flags) {
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ChangedPosData that = (ChangedPosData) o;
			return flags == that.flags && Objects.equals(state, that.state);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state, flags);
		}
	}
}
