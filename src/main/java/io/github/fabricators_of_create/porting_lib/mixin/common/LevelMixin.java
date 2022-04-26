package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

import net.minecraft.world.level.entity.EntityTypeTest;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.NeighborChangeListeningBlock;
import io.github.fabricators_of_create.porting_lib.block.WeakPowerCheckingBlock;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;

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
	private Map<BlockPos, ChangedPosData> port_lib$modifiedStates = null;

	@Unique
	private final SnapshotParticipant<LevelSnapshotData> port_lib$snapshotParticipant = new SnapshotParticipant<>() {

		@Override
		protected LevelSnapshotData createSnapshot() {
			LevelSnapshotData data = new LevelSnapshotData(port_lib$modifiedStates);
			if (port_lib$modifiedStates == null) port_lib$modifiedStates = new HashMap<>();
			return data;
		}

		@Override
		protected void readSnapshot(LevelSnapshotData snapshot) {
			port_lib$modifiedStates = snapshot.changedStates();
		}

		@Override
		protected void onFinalCommit() {
			super.onFinalCommit();
			Map<BlockPos, ChangedPosData> modifications = port_lib$modifiedStates;
			port_lib$modifiedStates = null;
			modifications.forEach((pos, data) -> setBlock(pos, data.state(), data.flags()));
		}
	};

	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Override
	public SnapshotParticipant<LevelSnapshotData> snapshotParticipant() {
		return port_lib$snapshotParticipant;
	}

	@Inject(method = "getBlockState", at = @At(value = "INVOKE", shift = Shift.BEFORE,
			target = "Lnet/minecraft/world/level/Level;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
		if (port_lib$modifiedStates != null) {
			ChangedPosData data = port_lib$modifiedStates.get(pos);
			if (data != null) cir.setReturnValue(data.state());
		}
	}

	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
			at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
	private void port_lib$setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
		if (port_lib$modifiedStates != null) {
			port_lib$modifiedStates.put(pos, new ChangedPosData(state, flags));
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
	public void port_lib$getRedstoneSignal(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockState port_lib$blockstate = MixinHelper.<Level>cast(this).getBlockState(blockPos);
		int port_lib$i = port_lib$blockstate.getSignal(MixinHelper.<Level>cast(this), blockPos, direction);

		if (port_lib$blockstate.getBlock() instanceof WeakPowerCheckingBlock) {
			cir.setReturnValue(
					((WeakPowerCheckingBlock) port_lib$blockstate.getBlock()).shouldCheckWeakPower(port_lib$blockstate, MixinHelper.<Level>cast(this), blockPos, direction)
							? Math.max(port_lib$i, MixinHelper.<Level>cast(this).getDirectSignalTo(blockPos))
							: port_lib$i);
		}
	}

	@Inject(
			method = "updateNeighbourForOutputSignal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$updateNeighbourForOutputSignal(BlockPos pos, Block block, CallbackInfo ci,
												   Iterator<?> var3, Direction direction, BlockPos blockPos2) {
		if (block instanceof NeighborChangeListeningBlock listener) {
			listener.onNeighborChange(getBlockState(blockPos2), this, blockPos2, pos);
		}
	}

	@Inject(
			method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Explosion;explode()V",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	@SuppressWarnings("ALL")
	public void port_lib$onStartExplosion(@Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
		if (ExplosionEvents.START.invoker().onExplosionStart((Level) (Object) this, explosion)) cir.setReturnValue(explosion);
	}

	@Inject(method = "method_31593", at = @At(value = "JUMP", opcode = Opcodes.IFEQ), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void port_lib$addGetEntities(Entity entity, Predicate<? super Entity> predicate, List<Entity> list, Entity entity2, CallbackInfo ci) {
		if (entity2 instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity() && !(entity instanceof EnderDragon)) { // dragons are already handled, skip
			PartEntity<?>[] parts = partEntity.getParts();
			if (parts != null) {
				for (PartEntity<?> part : partEntity.getParts()) {
					if (partEntity != entity && predicate.test(part)) {
						list.add(part);
					}
				}
			}
		}
	}

	@Group(name = "port_lib_part_entity_getting", min = 1, max = 1)
	// no idea why hashed is not remapping. So we have this mess.
	@Inject(method = "m_mbvohlyp", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, require = 0)
	private static <T extends Entity> void port_lib$partEntityGettingHashed(Predicate<? super T> predicate, List<T> list, EntityTypeTest<Entity, T> test, Entity entity, CallbackInfo ci) {
		port_lib$partEntityGetting(predicate, list, test, entity);
	}

	@Group(name = "port_lib_part_entity_getting", min = 1, max = 1)
	@Inject(method = "method_31596", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, require = 0)
	private static <T extends Entity> void port_lib$partEntityGettingIntermediary(Predicate<? super T> predicate, List<T> list, EntityTypeTest<Entity, T> test, Entity entity, CallbackInfo ci) {
		port_lib$partEntityGetting(predicate, list, test, entity);
	}

	private static <T extends Entity> void port_lib$partEntityGetting(Predicate<? super T> predicate, List<T> list, EntityTypeTest<Entity, T> test, Entity entity) {
		if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity() && !(entity instanceof EnderDragon)) { // dragons are already handled, skip
			PartEntity<?>[] parts = partEntity.getParts();
			if (parts != null) {
				for (PartEntity<?> part : partEntity.getParts()) {
					T t = test.tryCast(part);
					if (t != null && predicate.test(t)) {
						list.add(t);
					}
				}
			}
		}
	}
}
