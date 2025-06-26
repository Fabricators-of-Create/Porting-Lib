package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

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
}
