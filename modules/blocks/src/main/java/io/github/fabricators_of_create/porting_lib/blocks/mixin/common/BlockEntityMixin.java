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

	@Inject(at = @At("RETURN"), method = "saveAdditional")
	private void saveCustomData(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
		if (port_lib$extraData != null) {
			tag.put(BlockEntityDataKeys.EXTRA_DATA_KEY, port_lib$extraData.copy());
		}
	}

	@Inject(at = @At("RETURN"), method = "loadAdditional")
	private void loadCustomData(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
		tag.getCompound(BlockEntityDataKeys.EXTRA_DATA_KEY).ifPresent(data -> this.port_lib$extraData = data);
	}

	@Override
	public CompoundTag getPortingLibPersistentData() {
		if (port_lib$extraData == null) {
			port_lib$extraData = new CompoundTag();
		}
		return port_lib$extraData;
	}
}
