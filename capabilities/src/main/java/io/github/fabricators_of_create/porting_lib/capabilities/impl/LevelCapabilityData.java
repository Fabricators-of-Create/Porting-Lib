package io.github.fabricators_of_create.porting_lib.capabilities.impl;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class LevelCapabilityData extends SavedData
{
	public static final String ID = "capabilities";

	private INBTSerializable<CompoundTag> serializable;
	private CompoundTag capNBT = null;

	public LevelCapabilityData(@Nullable INBTSerializable<CompoundTag> serializable)
	{
		this.serializable = serializable;
	}

	public static LevelCapabilityData load(CompoundTag tag, @Nullable INBTSerializable<CompoundTag> serializable) {
		LevelCapabilityData data = new LevelCapabilityData(serializable);
		data.read(tag);
		return data;
	}

	public void read(CompoundTag nbt)
	{
		this.capNBT = nbt;
		if (serializable != null)
		{
			serializable.deserializeNBT(this.capNBT);
			this.capNBT = null;
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt)
	{
		if (serializable != null)
			nbt = serializable.serializeNBT();
		return nbt;
	}

	@Override
	public boolean isDirty()
	{
		return true;
	}

	public void setCapabilities(INBTSerializable<CompoundTag> capabilities)
	{
		this.serializable = capabilities;
		if (this.capNBT != null && serializable != null)
		{
			serializable.deserializeNBT(this.capNBT);
			this.capNBT = null;
		}
	}
}
