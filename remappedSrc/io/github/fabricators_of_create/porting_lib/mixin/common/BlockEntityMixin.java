package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.util.BlockEntityHelper;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExtensions, INBTSerializable<NbtCompound> {
	@Unique
	private NbtCompound port_lib$extraData = null;

	@Shadow
	public abstract void load(NbtCompound tag);

	@Shadow
	public abstract NbtCompound saveWithFullMetadata();

	@Inject(at = @At("RETURN"), method = "saveMetadata")
	private void port_lib$saveMetadata(NbtCompound nbt, CallbackInfo ci) {
		if (port_lib$extraData != null && !port_lib$extraData.isEmpty()) {
			nbt.put(BlockEntityHelper.EXTRA_DATA_KEY, port_lib$extraData);
		}
	}

	@Inject(at = @At("RETURN"), method = "load")
	private void port_lib$load(NbtCompound tag, CallbackInfo ci) {
		if (tag.contains(BlockEntityHelper.EXTRA_DATA_KEY)) {
			port_lib$extraData = tag.getCompound(BlockEntityHelper.EXTRA_DATA_KEY);
		} else if (tag.contains("create_ExtraEntityData")) {
			port_lib$extraData = tag.getCompound("create_ExtraEntityData");
		}
	}

	@Inject(method = "setRemoved", at = @At("TAIL"))
	public void port_lib$invalidate(CallbackInfo ci) {
		invalidateCaps();
	}

	@Override
	public NbtCompound serializeNBT() {
		return this.saveWithFullMetadata();
	}

	@Override
	public void deserializeNBT(NbtCompound nbt) {
		deserializeNBT(null, nbt);
	}

	@Override
	public NbtCompound getExtraCustomData() {
		if (port_lib$extraData == null) {
			port_lib$extraData = new NbtCompound();
		}
		return port_lib$extraData;
	}

	public void deserializeNBT(BlockState state, NbtCompound nbt) {
		this.load(nbt);
	}
}
