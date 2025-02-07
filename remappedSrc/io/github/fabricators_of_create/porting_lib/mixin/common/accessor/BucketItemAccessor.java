package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BucketItem.class)
public interface BucketItemAccessor {
	@Accessor("content")
	Fluid port_lib$getContent();
}
