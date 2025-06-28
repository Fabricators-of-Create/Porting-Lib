package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.util.BlockEntityDataKeys;
import net.minecraft.core.HolderLookup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.blocks.injects.BlockEntityInjection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityInjection {
	@Unique
	private CompoundTag port_lib$extraData = null;

	@Inject(at = @At("RETURN"), method = "saveMetadata")
	private void port_lib$saveMetadata(CompoundTag nbt, CallbackInfo ci) {
		if (port_lib$extraData != null && !port_lib$extraData.isEmpty()) {
			nbt.put(BlockEntityDataKeys.EXTRA_DATA_KEY, port_lib$extraData);
		}
	}

	@Inject(at = @At("RETURN"), method = "loadWithComponents")
	private void port_lib$load(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
		if (tag.contains(BlockEntityDataKeys.EXTRA_DATA_KEY)) {
			port_lib$extraData = tag.getCompound(BlockEntityDataKeys.EXTRA_DATA_KEY);
		}
	}

	@Override
	public CompoundTag getPortingLibPersistentData() {
		if (port_lib$extraData == null) {
			port_lib$extraData = new CompoundTag();
		}
		return port_lib$extraData;
	}
}
