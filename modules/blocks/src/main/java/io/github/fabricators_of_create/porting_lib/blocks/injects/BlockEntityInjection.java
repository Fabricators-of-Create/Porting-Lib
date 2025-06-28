package io.github.fabricators_of_create.porting_lib.blocks.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.nbt.CompoundTag;

public interface BlockEntityInjection {
	/**
	 * Gets a {@link CompoundTag} that can be used to store custom data for this block entity.
	 * It will be written, and read from disc, so it persists over world saves.
	 *
	 * @return A compound tag for custom persistent data
	 */
	default CompoundTag getPortingLibPersistentData() {
		throw PortingLib.createMixinException("BlockEntityInjection.getPortingLibPersistentData()");
	}
}
