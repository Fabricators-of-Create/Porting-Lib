package io.github.fabricators_of_create.porting_lib.core.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.UnknownNullability;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface INBTSerializable<T extends Tag> {
	@UnknownNullability
	T serializeNBT(HolderLookup.Provider provider);

	void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
