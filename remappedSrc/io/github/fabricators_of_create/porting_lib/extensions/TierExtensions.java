package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.block.Block;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public interface TierExtensions {
	@Nullable
	default TagKey<Block> getTag() {
		return null;
	}
}
