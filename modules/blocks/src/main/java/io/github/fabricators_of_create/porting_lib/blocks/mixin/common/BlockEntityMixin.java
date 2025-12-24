package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.util.BlockEntityDataKeys;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.world.level.storage.ValueOutput;

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

	@Inject(at = @At("HEAD"), method = "saveMetadata")
	private void saveExtraData(ValueOutput output, CallbackInfo ci) {
		if (this.port_lib$extraData != null)
			output.store(BlockEntityDataKeys.EXTRA_DATA_KEY, CompoundTag.CODEC, this.port_lib$extraData.copy());
	}

	@Inject(at = @At("HEAD"), method = "loadAdditional")
	private void loadExtraData(ValueInput input, CallbackInfo ci) {
		input.read(BlockEntityDataKeys.EXTRA_DATA_KEY, CompoundTag.CODEC).ifPresent(compoundTag -> this.port_lib$extraData = compoundTag);
	}

	@Override
	public CompoundTag getPortingLibPersistentData() {
		if (port_lib$extraData == null) {
			port_lib$extraData = new CompoundTag();
		}
		return port_lib$extraData;
	}
}
