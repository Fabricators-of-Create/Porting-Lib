package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.LinkedList;
import java.util.List;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import io.github.fabricators_of_create.porting_lib.extensions.common.LevelExtensions;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

import net.minecraft.world.level.chunk.LevelChunk;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = Level.class, priority = 1100) // need to apply after lithium
public abstract class LevelMixin implements LevelAccessor, LevelExtensions {
	// only non-null during transactions. Is set back to null in
	// onFinalCommit on commits, and through snapshot rollbacks on aborts.
	private List<ChangedPosData> port_lib$modifiedStates = null;

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
	private void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
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
	private void setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
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
			method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;ZLnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/Explosion;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Explosion;explode()V"
			),
			cancellable = true
	)
	public void onStartExplosion(Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound, CallbackInfoReturnable<Explosion> cir, @Local(ordinal = 0) Explosion explosion) {
		if (ExplosionEvents.START.invoker().onExplosionStart((Level) (Object) this, explosion)) {
			cir.setReturnValue(explosion);
		}
	}

	@Override
	public void port_lib$markAndNotifyBlock(BlockPos pos, @Nullable LevelChunk levelchunk, BlockState oldState, BlockState newState, int flags, int recursionLeft) {
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
