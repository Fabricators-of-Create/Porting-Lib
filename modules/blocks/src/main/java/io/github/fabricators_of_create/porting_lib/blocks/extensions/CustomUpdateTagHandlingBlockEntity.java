package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;

public interface CustomUpdateTagHandlingBlockEntity {
	/**
	 * Called when the chunk's BE update tag, gotten from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}, is received on the client.
	 * <p>
	 * Used to handle this tag in a special way. By default, this simply calls {@link BlockEntity#loadWithComponents(ValueInput)}.
	 *
	 * @param input The data sent from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}
	 */
	default void handleUpdateTag(ValueInput input) {
		((BlockEntity) this).loadWithComponents(input);
	}
}
