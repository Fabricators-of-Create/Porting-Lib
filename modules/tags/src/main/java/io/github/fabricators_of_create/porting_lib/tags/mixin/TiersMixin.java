package io.github.fabricators_of_create.porting_lib.tags.mixin;

import io.github.fabricators_of_create.porting_lib.tags.TagUtil;
import io.github.fabricators_of_create.porting_lib.tags.injects.TiersInjection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;

import net.minecraft.world.level.block.Block;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Tiers.class)
public class TiersMixin implements TiersInjection {
	@Override
	public @Nullable TagKey<Block> port_lib$getTag() {
		return TagUtil.getTagFromVanillaTier((Tiers) (Object) this);
	}
}
