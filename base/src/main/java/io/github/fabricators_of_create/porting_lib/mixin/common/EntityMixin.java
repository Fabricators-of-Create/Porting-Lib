package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Entity.class)
public abstract class EntityMixin implements INBTSerializableCompound {
	@Shadow
	public Level level;
	@Shadow
	public abstract EntityType<?> getType();
	@Shadow
	@Nullable
	protected abstract String getEncodeId();
	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);
	@Shadow
	public abstract void load(CompoundTag compoundTag);

	// RUNNING EFFECTS

	@Inject(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.BY,
					by = 2
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void port_lib$spawnSprintParticle(CallbackInfo ci, int i, int j, int k, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CustomRunningEffectsBlock custom &&
				custom.addRunningEffects(state, level, pos, (Entity) (Object) this)) {
			ci.cancel();
		}
	}

	@Unique
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", id);
		}
		return saveWithoutId(ret);
	}

	@Unique
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		load(nbt);
	}
}
