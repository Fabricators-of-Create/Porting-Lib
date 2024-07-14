package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.LevelExtensions;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.NeighborChangeListeningBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = Level.class, priority = 1100) // need to apply after lithium
public abstract class LevelMixin implements LevelAccessor, LevelExtensions {
	// only non-null during transactions. Is set back to null in
	// onFinalCommit on commits, and through snapshot rollbacks on aborts.
	@Unique
	private List<ChangedPosData> port_lib$modifiedStates = null;
	@Unique
	private final ArrayList<BlockEntity> port_lib$freshBlockEntities = new ArrayList<>();
	@Unique
	private final ArrayList<BlockEntity> port_lib$pendingFreshBlockEntities = new ArrayList<>();
	@Unique
	@Nullable
	private Integer port_lib$oldStateLight = null;

	@Unique
	private final SnapshotParticipant<LevelSnapshotData> port_lib$snapshotParticipant = new SnapshotParticipant<>() {

		@Override
		protected LevelSnapshotData createSnapshot() {
			LevelSnapshotData data = new LevelSnapshotData(port_lib$modifiedStates);
			if (port_lib$modifiedStates == null) port_lib$modifiedStates = new LinkedList<>();
			return data;
		}

		@Override
		protected void readSnapshot(LevelSnapshotData snapshot) {
			port_lib$modifiedStates = snapshot.changedStates();
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			List<ChangedPosData> modifications = port_lib$modifiedStates;
			port_lib$modifiedStates = null;
			for (ChangedPosData data : modifications) {
				setBlock(data.pos(), data.state(), data.flags());
			}
		}
	};

	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Shadow
	private boolean tickingBlockEntities;

	@Shadow
	public abstract ProfilerFiller getProfiler();

	@Shadow
	public abstract void setBlocksDirty(BlockPos pos, BlockState old, BlockState updated);

	@Shadow
	@Final
	public boolean isClientSide;

	@Shadow
	public abstract void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags);

	@Shadow
	public abstract void updateNeighbourForOutputSignal(BlockPos pos, Block block);

	@Shadow
	public abstract void onBlockStateChange(BlockPos pos, BlockState oldBlock, BlockState newBlock);

	@Override
	public SnapshotParticipant<LevelSnapshotData> snapshotParticipant() {
		return port_lib$snapshotParticipant;
	}

	@Inject(method = "getBlockState", at = @At(value = "INVOKE", shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
		if (port_lib$modifiedStates != null) {
			// iterate in reverse order - latest changes priority
			for (ChangedPosData data : port_lib$modifiedStates) {
				if (data.pos().equals(pos)) {
					BlockState state = data.state();
					if (state == null) {
						PortingLib.LOGGER.error("null blockstate stored in snapshots at " + pos);
						new Throwable().printStackTrace();
					} else {
						cir.setReturnValue(state);
					}
					return;
				}
			}
		}
	}

	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
		if (state == null) {
			PortingLib.LOGGER.error("Setting null blockstate at " + pos);
			new Throwable().printStackTrace();
		}
		if (port_lib$modifiedStates != null) {
			port_lib$modifiedStates.add(new ChangedPosData(pos, state, flags));
			cir.setReturnValue(true);
		}
	}

	@Inject(
			method = "updateNeighbourForOutputSignal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = Shift.BY,
					by = 2,
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$updateNeighbourForOutputSignal(BlockPos pos, Block block, CallbackInfo ci,
														Iterator<?> var3, Direction direction, BlockPos offset,
														BlockState state) {
		if (state.getBlock() instanceof NeighborChangeListeningBlock listener) {
			listener.onNeighborChange(state, this, offset, pos);
		}
	}

//	@Inject( TODO: PORT
//			method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;ZLnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/sounds/SoundEvent;)Lnet/minecraft/world/level/Explosion;",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/level/Explosion;explode()V"
//			),
//			locals = LocalCapture.CAPTURE_FAILHARD,
//			cancellable = true
//	)
//	public void port_lib$onStartExplosion(Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionDamageCalculator,
//										  double d, double e, double f, float g, boolean bl, Level.ExplosionInteraction explosionInteraction,
//										  boolean bl2, ParticleOptions particleOptions, ParticleOptions particleOptions2, SoundEvent soundEvent,
//										  CallbackInfoReturnable<Explosion> cir, @Local(ordinal = 0) Explosion explosion) {
//		if (ExplosionEvents.START.invoker().onExplosionStart((Level) (Object) this, explosion)) {
//			cir.setReturnValue(explosion);
//		}
//	}

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = Shift.AFTER))
	public void port_lib$pendingBlockEntities(CallbackInfo ci) {
		if (!this.port_lib$pendingFreshBlockEntities.isEmpty()) {
			this.port_lib$freshBlockEntities.addAll(this.port_lib$pendingFreshBlockEntities);
			this.port_lib$pendingFreshBlockEntities.clear();
		}
	}

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	public void port_lib$onBlockEntitiesLoad(CallbackInfo ci) {
		if (!this.port_lib$freshBlockEntities.isEmpty()) {
			this.port_lib$freshBlockEntities.forEach(BlockEntityExtensions::onLoad);
			this.port_lib$freshBlockEntities.clear();
		}
	}

	@Unique
	@Override
	public void addFreshBlockEntities(Collection<BlockEntity> beList) {
		if (this.tickingBlockEntities) {
			this.port_lib$pendingFreshBlockEntities.addAll(beList);
		} else {
			this.port_lib$freshBlockEntities.addAll(beList);
		}
	}

	@Unique
	@Override
	public void markAndNotifyBlock(BlockPos pos, @Nullable LevelChunk levelchunk, BlockState oldState, BlockState newState, int flags, int recursionLeft) {
		Block block = newState.getBlock();
		BlockState blockstate1 = getBlockState(pos);
		{
			{
				if (blockstate1 == newState) {
					if (oldState != blockstate1) {
						this.setBlocksDirty(pos, oldState, blockstate1);
					}

					if ((flags & 2) != 0 && (!this.isClientSide || (flags & 4) == 0) && (this.isClientSide || levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
						this.sendBlockUpdated(pos, oldState, newState, flags);
					}

					if ((flags & 1) != 0) {
						this.blockUpdated(pos, oldState.getBlock());
						if (!this.isClientSide && newState.hasAnalogOutputSignal()) {
							this.updateNeighbourForOutputSignal(pos, block);
						}
					}

					if ((flags & 16) == 0 && recursionLeft > 0) {
						int i = flags & -34;
						oldState.updateIndirectNeighbourShapes(this, pos, i, recursionLeft - 1);
						newState.updateNeighbourShapes(this, pos, i, recursionLeft - 1);
						newState.updateIndirectNeighbourShapes(this, pos, i, recursionLeft - 1);
					}

					this.onBlockStateChange(pos, oldState, blockstate1);
				}
			}
		}
	}
}
