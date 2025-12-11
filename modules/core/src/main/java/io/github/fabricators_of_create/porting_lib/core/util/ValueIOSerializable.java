package io.github.fabricators_of_create.porting_lib.core.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A generic interface for objects that can be serialized to a {@link ValueOutput} and deserialized from a {@link ValueInput}
 */
public interface ValueIOSerializable {
	void serialize(ValueOutput output);

	void deserialize(ValueInput input);
}
