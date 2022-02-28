package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TierExtensions;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Tiers;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Tiers.class)
public class TiersMixin implements TierExtensions {
	@Nullable
	@Override
	public Tag.Named<Block> getTag() {
		return TagUtil.getTagFromVanillaTier((Tiers) (Object) this);
	}
}
