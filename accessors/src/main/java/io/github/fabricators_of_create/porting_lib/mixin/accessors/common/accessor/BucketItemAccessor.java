package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

@Mixin(BucketItem.class)
public interface BucketItemAccessor {
	@Accessor("content")
	Fluid port_lib$getContent();
}
