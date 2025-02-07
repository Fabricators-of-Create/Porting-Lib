package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
	@Invoker("saveMetadata")
	void port_lib$saveMetadata(NbtCompound compoundTag);
}
