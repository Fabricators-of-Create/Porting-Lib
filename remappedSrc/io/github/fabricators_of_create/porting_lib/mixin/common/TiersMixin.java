package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.TierExtensions;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ToolMaterials.class)
public abstract class TiersMixin implements TierExtensions {
	@Nullable
	@Override
	public TagKey<Block> getTag() {
		return TagUtil.getTagFromVanillaTier((ToolMaterials) (Object) this);
	}
}
