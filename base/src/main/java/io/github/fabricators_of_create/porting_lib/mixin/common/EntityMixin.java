package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Mixin(Entity.class)
public abstract class EntityMixin implements INBTSerializableCompound {
	@Shadow
	public abstract EntityType<?> getType();
	@Shadow
	@Nullable
	protected abstract String getEncodeId();
	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);

	@Shadow
	public abstract void load(CompoundTag nbt);

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
