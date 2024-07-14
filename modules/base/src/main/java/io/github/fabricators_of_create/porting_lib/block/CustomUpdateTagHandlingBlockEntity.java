package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface CustomUpdateTagHandlingBlockEntity {
	/**
	 * Called when the chunk's BE update tag, gotten from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}, is received on the client.
	 * <p>
	 * Used to handle this tag in a special way. By default this simply calls {@link BlockEntity#loadWithComponents(CompoundTag, HolderLookup.Provider)}.
	 *
	 * @param tag The {@link CompoundTag} sent from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}
	 */
	default void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		((BlockEntity) this).loadWithComponents(tag, lookupProvider);
	}
}
