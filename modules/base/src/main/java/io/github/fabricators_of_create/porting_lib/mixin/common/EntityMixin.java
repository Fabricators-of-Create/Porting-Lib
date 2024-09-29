package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

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
public abstract class EntityMixin implements INBTSerializable<CompoundTag> {
	@Shadow
	private Level level;
	@Shadow
	public abstract EntityType<?> getType();
	@Shadow
	@Nullable
	protected abstract String getEncodeId();
	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);

	@Shadow
	public abstract void load(CompoundTag nbt);

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		load(nbt);
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag ret = new CompoundTag();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", getEncodeId());
		}
		return saveWithoutId(ret);
	}

	// RUNNING EFFECTS

	@Inject(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.BY,
					by = 2
			),
			cancellable = true
	)
	public void port_lib$spawnSprintParticle(CallbackInfo ci, @Local BlockPos pos, @Local BlockState state) {
		//noinspection ConstantValue
		if (state.getBlock() instanceof CustomRunningEffectsBlock custom &&
				custom.addRunningEffects(state, level, pos, (Entity) (Object) this)) {
			ci.cancel();
		}
	}


}
