package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.tags.Tag;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public interface TierExtensions {
	@Nullable
	default Tag.Named<Block> getTag() {
		return null;
	}
}
