package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.Tag;

/**
 * @deprecated use {@link io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable} instead
 */
@Deprecated(forRemoval = true)
public interface INBTSerializable<T extends Tag> {
	T serializeNBT();

	void deserializeNBT(T nbt);
}
