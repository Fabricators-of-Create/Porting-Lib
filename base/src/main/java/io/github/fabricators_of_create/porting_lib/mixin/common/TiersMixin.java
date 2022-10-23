package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.TierExtensions;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;

@Mixin(Tiers.class)
public abstract class TiersMixin implements TierExtensions {
	@Nullable
	@Override
	public TagKey<Block> getTag() {
		return TagUtil.getTagFromVanillaTier((Tiers) (Object) this);
	}
}
