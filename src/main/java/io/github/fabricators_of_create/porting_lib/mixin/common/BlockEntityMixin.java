package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.util.BlockEntityHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExtensions, NBTSerializable {
	@Unique
	private CompoundTag port_lib$extraData = null;

	@Shadow
	public abstract void load(CompoundTag tag);

	@Inject(at = @At("RETURN"), method = "saveMetadata")
	private void port_lib$saveMetadata(CompoundTag nbt, CallbackInfo ci) {
		if (port_lib$extraData != null && !port_lib$extraData.isEmpty()) {
			nbt.put(BlockEntityHelper.EXTRA_DATA_KEY, port_lib$extraData);
		}
	}

	@Inject(at = @At("RETURN"), method = "load")
	private void port_lib$load(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(BlockEntityHelper.EXTRA_DATA_KEY)) {
			port_lib$extraData = tag.getCompound(BlockEntityHelper.EXTRA_DATA_KEY);
		}
	}

	@Override
	public CompoundTag port_lib$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		this.load(nbt);
		return nbt;
	}

	@Override
	public void port_lib$deserializeNBT(CompoundTag nbt) {
		deserializeNBT(null, nbt);
	}

	@Override
	public CompoundTag getExtraCustomData() {
		if (port_lib$extraData == null) {
			port_lib$extraData = new CompoundTag();
		}
		return port_lib$extraData;
	}

	public void deserializeNBT(BlockState state, CompoundTag nbt) {
		this.load(nbt);
	}
}
