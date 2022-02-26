package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExtensions, NBTSerializable {
	@Override
	public CompoundTag port_lib$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		MixinHelper.<BlockEntity>cast(this).load(nbt);
		return nbt;
	}

	@Override
	public void port_lib$deserializeNBT(CompoundTag nbt) {
		port_lib$deserializeNBT(null, nbt);
	}

	public void port_lib$deserializeNBT(BlockState state, CompoundTag nbt) {
		MixinHelper.<BlockEntity>cast(this).load(nbt);
	}
}
