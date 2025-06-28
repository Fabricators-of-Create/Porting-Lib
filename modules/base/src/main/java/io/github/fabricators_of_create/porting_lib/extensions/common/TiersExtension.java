package io.github.fabricators_of_create.porting_lib.extensions.common;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public interface TiersExtension {
	@Nullable
	default TagKey<Block> getTag() {
		return null;
	}
}
